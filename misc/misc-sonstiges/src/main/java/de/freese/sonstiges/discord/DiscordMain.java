// Created: 07.02.2021
package de.freese.sonstiges.discord;

import java.awt.Color;

import de.freese.sonstiges.discord.message.DiscordAuthor;
import de.freese.sonstiges.discord.message.DiscordEmbed;
import de.freese.sonstiges.discord.message.DiscordField;
import de.freese.sonstiges.discord.message.DiscordFooter;
import de.freese.sonstiges.discord.message.DiscordImage;
import de.freese.sonstiges.discord.message.DiscordThumbnail;
import de.freese.sonstiges.discord.message.DiscordWebHookMessage;

/**
 * @author Thomas Freese
 */
public final class DiscordMain {
    public static void main(final String[] args) throws Exception {
        // URL für den WebHook: Servereinstellungen -> Integrationen -> WebHooks anzeigen -> WebHook-URL kopieren
        //        String webHookId = args[0];
        //        String webHookToken = args[1];

        // URL für Demo-Bild.
        String iconUrl = "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_92x30dp.png";

        DiscordWebHookMessage message = new DiscordWebHookMessage();
        message.setTts(false);
        message.setAvatarUrl("https://avatars.githubusercontent.com/u/1973918?v=4");
        message.setAvatarUsername("Avatar Username");
        message.setAvatarContent("Avatar Content");

        // Ein einzelner Abschnitt / Nachrichtenblock.
        DiscordEmbed embed = new DiscordEmbed();
        embed.setColor(Color.RED);
        embed.setAuthor(new DiscordAuthor().setName("Author Name").setNameUrl("https://discord.com/developers/docs/resources/webhook").setIconUrl(iconUrl));
        embed.setTitle("Title").setTitleDescription("Title description").setTitleUrl("https://google.de");
        embed.setThumbnail(new DiscordThumbnail(iconUrl));// Kleines Bild oben Rechts
        embed.addField(new DiscordField().setName("1st Field").setValue("Inline - [Google](https://www.google.de)").setInline(true));
        embed.addField(new DiscordField().setName("2nd Field").setValue("Inline - Text").setInline(true));
        embed.addField(new DiscordField().setName("3nd Field").setValue("Not Inline - Text"));
        embed.setFooter(new DiscordFooter().setText("Footer Text").setIconUrl(iconUrl));
        embed.setImage(new DiscordImage(iconUrl)); // Großes Bild
        message.addEmbed(embed);

        // Minimaler Abschnitt.
        message.addEmbed(new DiscordEmbed().setColor(Color.GREEN).setTitleDescription("Just another added embed object!"));

        // Message senden.
        DiscordWebHookSender discordWebHookSender = new DefaultDiscordWebHookSender();
        System.out.println(discordWebHookSender.toJson(message));
        //        discordWebHookSender.send(message, webHookId, webHookToken);
    }

    private DiscordMain() {
        super();
    }
}
