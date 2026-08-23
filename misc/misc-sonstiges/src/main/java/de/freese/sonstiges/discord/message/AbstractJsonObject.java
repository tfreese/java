package de.freese.sonstiges.discord.message;

/**
 * Mapping by type-Property to specific Classes.
 *
 * @author Thomas Freese
 * @since 09.05.2025
 */
// @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
// @JsonSubTypes({
//         @JsonSubTypes.Type(value = ObjectA.class, name = "ObjectA"),
//         @JsonSubTypes.Type(value = ObjectB.class, name = "ObjectB")
// })
public abstract class AbstractJsonObject {
}
