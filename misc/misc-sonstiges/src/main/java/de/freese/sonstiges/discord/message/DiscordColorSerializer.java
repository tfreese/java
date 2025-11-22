// Created: 06.07.2021
package de.freese.sonstiges.discord.message;

import java.awt.Color;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

/**
 * @author Thomas Freese
 */
public class DiscordColorSerializer extends StdSerializer<Color> {

    public DiscordColorSerializer() {
        this(null);
    }

    public DiscordColorSerializer(final Class<Color> clazz) {
        super(clazz);
    }

    @Override
    public void serialize(final Color value, final JsonGenerator gen, final SerializationContext provider) throws JacksonException {
        int rgb = value.getRed();
        rgb = (rgb << 8) + value.getGreen();
        rgb = (rgb << 8) + value.getBlue();

        gen.writeNumber(rgb);
    }
}
