// Created: 27.03.2018
package de.freese.maven.proxy.netty;

import java.net.URI;
import java.util.Objects;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedStream;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.maven.proxy.repository.Repository;
import de.freese.maven.proxy.repository.RepositoryResponse;
import de.freese.maven.proxy.util.ProxyUtils;

/**
 * Handler für Requests an den Maven Proxy.
 *
 * @author Thomas Freese
 */
public class NettyMavenRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    /**
     * (0x0D, 0x0A), (13,10), (\r\n)
     */
    private static final String CRLF = "\r\n";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Repository repository;

    public NettyMavenRequestHandler(final Repository repository) {
        super();

        this.repository = Objects.requireNonNull(repository, "repository required");
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, cause.getMessage(), null);
        }
        else {
            getLogger().error(cause.getMessage(), cause);
        }
    }

    /**
     * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception {
        if ("/".equals(request.uri())) {
            sendError(ctx, HttpResponseStatus.NOT_FOUND, "File not found: /", request);

            return;
        }

        if (HttpMethod.HEAD.equals(request.method())) {
            handleHead(ctx, request);
        }
        else if (HttpMethod.GET.equals(request.method())) {
            handleGet(ctx, request);
        }
        else if (HttpMethod.PUT.equals(request.method())) {
            // deploy
            handlePut(ctx, request);
        }
        else if (HttpMethod.CONNECT.equals(request.method())) {
            // Proxy
            // String content = request.content().toString(CharsetUtil.UTF_8);

            ByteBuf byteBuf = Unpooled.copiedBuffer("", CharsetUtil.UTF_8);
            // FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, new HttpResponseStatus(200, "Connection established"), byteBuf);
            response.headers().set("Proxy-Agent", "Maven-Proxy");
            response.headers().set(HttpHeaderNames.CONNECTION, "close");

            ctx.writeAndFlush(response);
        }
        else {
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED, request.method() + "; " + request.uri(), request);
        }
    }

    protected Logger getLogger() {
        return this.logger;
    }

    protected void handleGet(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception {
        final boolean keepAlive = HttpUtil.isKeepAlive(request);
        final URI uri = new URI(request.uri());

        RepositoryResponse repositoryResponse = this.repository.getInputStream(uri);

        if (repositoryResponse == null) {
            sendError(ctx, HttpResponseStatus.NOT_FOUND, "File not found: " + uri, request);

            return;
        }

        long fileLength = repositoryResponse.getContentLength();

        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.SERVER, "Maven-Proxy");
        HttpUtil.setContentLength(response, fileLength);

        // setContentTypeHeader(response, file);

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, ProxyUtils.getContentType(repositoryResponse.getFileName()));

        // setDateAndCacheHeaders(response, file);

        if (!keepAlive) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }
        else if (request.protocolVersion().equals(HttpVersion.HTTP_1_0)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        // Write the initial line and the header.
        ctx.write(response);

        // Write the content.
        ChannelFuture sendFileFuture = null;
        ChannelFuture lastContentFuture = null;

        if (ctx.pipeline().get(SslHandler.class) == null) {
            sendFileFuture = ctx.write(new HttpChunkedInput(new ChunkedStream(repositoryResponse.getInputStream())), ctx.newProgressivePromise());

            // Write the end marker.
            lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        }
        else {
            sendFileFuture = ctx.writeAndFlush(new HttpChunkedInput(new ChunkedStream(repositoryResponse.getInputStream())), ctx.newProgressivePromise());

            // HttpChunkedInput will write the end marker (LastHttpContent) for us.
            lastContentFuture = sendFileFuture;
        }

        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            /**
             * @see io.netty.util.concurrent.GenericFutureListener#operationComplete(io.netty.util.concurrent.Future)
             */
            @Override
            public void operationComplete(final ChannelProgressiveFuture future) {
                // getLogger().debug(future.channel() + " Transfer complete: " + request.uri());
                getLogger().debug("Transfer complete: {}", request.uri());
            }

            /**
             * @see io.netty.util.concurrent.GenericProgressiveFutureListener#operationProgressed(io.netty.util.concurrent.ProgressiveFuture, long, long)
             */
            @Override
            public void operationProgressed(final ChannelProgressiveFuture future, final long progress, final long total) {
                if (total < 0) {
                    // total unknown
                    getLogger().debug("{}: Transfer progress: {} : {}", future.channel(), progress, request.uri());
                }
                else {
                    getLogger().debug("{}: Transfer progress: {} / {} : {}", future.channel(), progress, total, request.uri());
                }
            }
        });

        // Decide whether to close the connection or not.
        if (!keepAlive) {
            // Close the connection when the whole content is written out.
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    protected void handleHead(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception {
        final URI uri = new URI(request.uri());

        boolean exist = this.repository.exist(uri);

        HttpResponseStatus responseStatus = exist ? HttpResponseStatus.OK : HttpResponseStatus.NOT_FOUND;

        // HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, responseStatus);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseStatus);

        sendAndCleanupConnection(ctx, response, request);
    }

    protected void handlePut(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception {
        sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED, request.uri(), request);
    }

    /**
     * If Keep-Alive is disabled, attaches "Connection: close" header to the response and closes the connection after the response being sent.
     */
    protected void sendAndCleanupConnection(final ChannelHandlerContext ctx, final FullHttpResponse response, final FullHttpRequest request) {
        final boolean keepAlive = request == null || HttpUtil.isKeepAlive(request);

        response.headers().set(HttpHeaderNames.SERVER, "Maven-Proxy");
        HttpUtil.setContentLength(response, response.content().readableBytes());

        if (!keepAlive) {
            // We're going to close the connection as soon as the response is sent,
            // so we should also make it clear for the client.
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }
        else if ((request != null) && request.protocolVersion().equals(HttpVersion.HTTP_1_0)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ChannelFuture flushPromise = ctx.writeAndFlush(response);

        if (!keepAlive) {
            // Close the connection as soon as the response is sent.
            flushPromise.addListener(ChannelFutureListener.CLOSE);
        }
    }

    protected void sendError(final ChannelHandlerContext ctx, final HttpResponseStatus status, final String message, final FullHttpRequest request) {
        getLogger().error("HTTP-Failure: {}; Message: {}", status, message);

        StringBuilder sb = new StringBuilder();
        sb.append("HTTP-Failure: ").append(status).append(CRLF);

        if ((message != null) && !message.isBlank()) {
            sb.append("Message: ").append(message).append(CRLF);
        }

        ByteBuf byteBuf = Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8);

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, byteBuf);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        sendAndCleanupConnection(ctx, response, request);
    }
}
