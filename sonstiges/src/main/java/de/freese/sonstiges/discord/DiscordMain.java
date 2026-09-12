package de.freese.sonstiges.discord;

import java.awt.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.discord.message.DiscordAuthor;
import de.freese.sonstiges.discord.message.DiscordEmbed;
import de.freese.sonstiges.discord.message.DiscordField;
import de.freese.sonstiges.discord.message.DiscordFooter;
import de.freese.sonstiges.discord.message.DiscordImage;
import de.freese.sonstiges.discord.message.DiscordThumbnail;
import de.freese.sonstiges.discord.message.DiscordWebHookMessage;

/**
 * @author Thomas Freese
 * @since 07.02.2021
 */
public final class DiscordMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordMain.class);

    static void main() {
        try {
            final DiscordWebHookMessage message = createMessage();

            final DiscordWebHookSender discordWebHookSender = new DefaultDiscordWebHookSender();
            LOGGER.atInfo().log(discordWebHookSender.toJson(message));

            // final String webHookId = "your_webhook_id";
            // final String webHookToken = "your_webhook_token";
            // discordWebHookSender.send(message, webHookId, webHookToken);
        }
        catch (final Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private static DiscordWebHookMessage createMessage() {
        final String iconUrl = "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_92x30dp.png";

        final DiscordWebHookMessage message = new DiscordWebHookMessage();
        message.setTts(false);
        message.setAvatarUrl("https://avatars.githubusercontent.com/u/1973918?v=4");
        message.setAvatarUsername("Avatar Username");
        message.setAvatarContent("Avatar Content");

        // News-Block.
        final DiscordEmbed embed = new DiscordEmbed();
        embed.setColor(Color.RED);
        embed.setAuthor(new DiscordAuthor().setName("Author Name").setNameUrl("https://discord.com/developers/docs/resources/webhook").setIconUrl(iconUrl));
        embed.setTitle("Title").setTitleDescription("Title description").setTitleUrl("https://google.de");
        embed.setThumbnail(new DiscordThumbnail(iconUrl)); // Kleines Bild oben Rechts
        embed.addField(new DiscordField().setName("1st Field").setValue("Inline - [Google](https://www.google.de)").setInline(true));
        embed.addField(new DiscordField().setName("2nd Field").setValue("Inline - Text").setInline(true));
        embed.addField(new DiscordField().setName("3nd Field").setValue("Not Inline - Text"));
        embed.setFooter(new DiscordFooter().setText("Footer Text").setIconUrl(iconUrl));
        embed.setImage(new DiscordImage(iconUrl)); // Großes Bild
        message.addEmbed(embed);

        // Simple Message.
        message.addEmbed(new DiscordEmbed().setColor(Color.GREEN).setTitleDescription("Just another added embed object!"));

        return message;
    }

    private DiscordMain() {
        super();
    }
}
