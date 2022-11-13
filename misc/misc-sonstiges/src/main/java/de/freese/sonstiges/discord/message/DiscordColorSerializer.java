// Created: 06.07.2021
package de.freese.sonstiges.discord.message;

import java.awt.Color;
import java.io.IOException;
import java.io.Serial;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * @author Thomas Freese
 */
public class DiscordColorSerializer extends StdSerializer<Color>
{
    @Serial
    private static final long serialVersionUID = 8153995700042146023L;

    public DiscordColorSerializer()
    {
        this(null);
    }

    public DiscordColorSerializer(final Class<Color> clazz)
    {
        super(clazz);
    }

    /**
     * @see com.fasterxml.jackson.databind.ser.std.StdSerializer#serialize(java.lang.Object, com.fasterxml.jackson.core.JsonGenerator,
     * com.fasterxml.jackson.databind.SerializerProvider)
     */
    @Override
    public void serialize(final Color value, final JsonGenerator gen, final SerializerProvider provider) throws IOException
    {
        int rgb = value.getRed();
        rgb = (rgb << 8) + value.getGreen();
        rgb = (rgb << 8) + value.getBlue();

        gen.writeNumber(rgb);
    }
}
