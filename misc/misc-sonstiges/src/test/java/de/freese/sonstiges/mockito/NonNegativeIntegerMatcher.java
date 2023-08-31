// Created: 06.11.2013
package de.freese.sonstiges.mockito;

import org.mockito.ArgumentMatcher;

/**
 * @author Thomas Freese
 */
public class NonNegativeIntegerMatcher implements ArgumentMatcher<Integer> // CustomMatcher<Integer>
{
    @Override
    public boolean matches(final Integer argument) {
        return argument < 0;
    }
}
