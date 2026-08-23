package de.freese.binding;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Objects;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 * @since 09.08.2026
 */
@SuppressWarnings({"unchecked", "java:S1192"})
public final class SwingBindings {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwingBindings.class);

    public static void bindToProperty(final JCheckBox component, final Property<Boolean> property) {
        component.addItemListener(event -> {
            final boolean selected = component.isSelected();

            LOGGER.debug("JCheckBox selection changed: {}", selected);

            if (Objects.equals(selected, property.getValue())) {
                LOGGER.debug("JCheckBox.isSelected() equals Property.getValue() -> return: {}", selected);
                return;
            }

            property.setValue(selected);
        });
    }

    public static <T> void bindToProperty(final JComboBox<T> component, final Property<T> property) {
        component.addItemListener(event -> {
            final T selectedItem = (T) component.getSelectedItem();

            LOGGER.debug("JComboBox selection changed: {}", selectedItem);

            if (Objects.equals(selectedItem, property.getValue())) {
                LOGGER.debug("JComboBox.getSelectedItem() equals Property.getValue() -> return: {}", selectedItem);
                return;
            }

            property.setValue(selectedItem);
        });
    }

    public static void bindToProperty(final JSlider component, final Property<Integer> property) {
        component.addChangeListener(event -> {
            final int value = component.getValue();

            LOGGER.debug("JSlider changed: {}", value);

            if (Objects.equals(value, property.getValue())) {
                LOGGER.debug("JSlider.getValue() equals Property.getValue() -> return: {}", value);
                return;
            }

            property.setValue(value);
        });
    }

    public static <T> void bindToProperty(final JSpinner component, final Property<T> property) {
        component.addChangeListener(event -> {
            final T value = (T) component.getValue();

            LOGGER.debug("JSpinner changed: {}", value);

            if (Objects.equals(value, property.getValue())) {
                LOGGER.debug("JSpinner.getValue() equals Property.getValue() -> return: {}", value);
                return;
            }

            property.setValue(value);
        });
    }

    public static void bindToProperty(final JTextComponent component, final Property<String> property) {
        component.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent e) {
                final String text = component.getText();

                LOGGER.debug("JTextComponent focus lost: {}", text);

                if (Objects.equals(text, property.getValue())) {
                    LOGGER.debug("JTextComponent.getText() equals Property.getValue() -> return: {}", text);
                    return;
                }

                property.setValue(text);
            }
        });
    }

    public static void bindToProperty(final JLabel component, final Property<String> property) {
        component.addPropertyChangeListener("text", event -> {
            final String text = component.getText();

            LOGGER.debug("JLabel text changed: {}", text);

            if (Objects.equals(text, property.getValue())) {
                LOGGER.debug("JLabel.getText() equals Property.getValue() -> return: {}", text);
                return;
            }

            property.setValue(text);
        });
    }

    public static <T> void bindToSwing(final Property<T> property, final JSpinner component) {
        property.addListener((prop, oldValue, newValue) -> {
            if (Objects.equals(component.getValue(), newValue)) {
                LOGGER.debug("JSpinner.getValue() equals newValue -> return: {}", newValue);
                return;
            }

            component.setValue(newValue);
        });
    }

    public static void bindToSwing(final Property<Integer> property, final JSlider component) {
        property.addListener((prop, oldValue, newValue) -> {
            if (Objects.equals(component.getValue(), newValue)) {
                LOGGER.debug("JSlider.getValue() equals newValue -> return: {}", newValue);
                return;
            }

            component.setValue(newValue);
        });
    }

    public static void bindToSwing(final Property<String> property, final JLabel component) {
        property.addListener((prop, oldValue, newValue) -> {
            if (Objects.equals(component.getText(), newValue)) {
                LOGGER.debug("JLabel.getText() equals newValue -> return: {}", newValue);
                return;
            }

            component.setText(newValue);
        });
    }

    public static void bindToSwing(final Property<String> property, final JTextComponent component) {
        property.addListener((prop, oldValue, newValue) -> {
            if (Objects.equals(component.getText(), newValue)) {
                LOGGER.debug("JTextComponent.getText() equals newValue -> return: {}", newValue);
                return;
            }

            component.setText(newValue);
        });
    }

    public static void bindToSwing(final Property<String> property, final TitledBorder component) {
        property.addListener((prop, oldValue, newValue) -> {
            if (Objects.equals(component.getTitle(), newValue)) {
                LOGGER.debug("TitledBorder.getTitle() equals newValue -> return: {}", newValue);
                return;
            }

            component.setTitle(newValue);
        });
    }

    public static <T> void bindToSwing(final Property<T> property, final JComboBox<T> component) {
        property.addListener((prop, oldValue, newValue) -> {
            if (Objects.equals(component.getSelectedItem(), newValue)) {
                LOGGER.debug("JComboBox.getSelectedItem() equals newValue -> return: {}", newValue);
                return;
            }

            component.setSelectedItem(newValue);
        });
    }

    public static void bindToSwing(final Property<Boolean> property, final JCheckBox component) {
        property.addListener((prop, oldValue, newValue) -> {
            if (Objects.equals(component.isSelected(), newValue)) {
                LOGGER.debug("JCheckBox.isSelected() equals newValue -> return: {}", newValue);
                return;
            }

            component.setSelected(newValue);
        });
    }

    private SwingBindings() {
        super();
    }
}
