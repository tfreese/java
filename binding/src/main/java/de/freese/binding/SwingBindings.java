// Created: 18.08.2018
package de.freese.binding;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Objects;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.binding.property.Property;
import de.freese.binding.value.ObservableValue;

/**
 * Util-Klasse für die Swing-Bindings.
 *
 * @author Thomas Freese
 */
@SuppressWarnings("unchecked")
public final class SwingBindings {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwingBindings.class);

    public static void bindBidirectional(final JCheckBox component, final Property<Boolean> property) {
        bindToSwing(property, component);
        bindToProperty(component, property);
    }

    public static <T> void bindBidirectional(final JComboBox<T> component, final Property<T> property) {
        bindToSwing(property, component);
        bindToProperty(component, property);
    }

    public static void bindBidirectional(final JSlider component, final Property<Integer> property) {
        bindToSwing(property, component);
        bindToProperty(component, property);
    }

    public static void bindBidirectional(final JSpinner component, final Property<Integer> property) {
        bindToSwing(property, component);
        bindToProperty(component, property);
    }

    public static void bindBidirectional(final JTextComponent component, final Property<String> property) {
        bindToSwing(property, component);
        bindToProperty(component, property);
    }

    public static void bindToProperty(final JCheckBox component, final Property<Boolean> property) {
        component.addItemListener(event -> {
            boolean selected = component.isSelected();

            if (Objects.equals(selected, property.getValue())) {
                LOGGER.debug("JCheckBox: Selected equals property.getValue() -> return: {}", selected);
                return;
            }

            LOGGER.debug("JCheckBox selection changed: {}", selected);

            updateProperty(property, selected);
        });
    }

    public static <T> void bindToProperty(final JComboBox<T> component, final Property<T> property) {
        component.addItemListener(event -> {
            T selectedItem = (T) component.getSelectedItem();

            if (Objects.equals(selectedItem, property.getValue())) {
                LOGGER.debug("JComboBox: SelectedItem equals property.getValue() -> return: {}", selectedItem);
                return;
            }

            LOGGER.debug("JComboBox selection changed: {}", selectedItem);

            updateProperty(property, selectedItem);
        });
    }

    public static void bindToProperty(final JSlider component, final Property<Integer> property) {
        component.addChangeListener(event -> {
            int value = component.getValue();

            if (Objects.equals(value, property.getValue())) {
                LOGGER.debug("JSlider: Value equals property.getValue() -> return: {}", value);
                return;
            }

            LOGGER.debug("JSlider changed: {}", value);

            updateProperty(property, value);
        });
    }

    public static <T> void bindToProperty(final JSpinner component, final Property<T> property) {
        component.addChangeListener(event -> {
            T value = (T) component.getValue();

            if (Objects.equals(value, property.getValue())) {
                LOGGER.debug("JSpinner: Value equals property.getValue() -> return: {}", value);
                return;
            }

            LOGGER.debug("JSpinner changed: {}", value);

            updateProperty(property, value);
        });
    }

    public static void bindToProperty(final JTextComponent component, final Property<String> property) {
        component.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent e) {
                String text = component.getText();

                if (Objects.equals(text, property.getValue())) {
                    LOGGER.debug("JTextComponent: Text equals property.getValue() -> return: {}", text);
                    return;
                }

                LOGGER.debug("JTextComponent focus lost: {}", text);

                updateProperty(property, text);
            }
        });
    }

    public static void bindToSwing(final ObservableValue<Boolean> value, final JCheckBox component) {
        value.addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(component.isSelected(), newValue)) {
                LOGGER.debug("JCheckBox: Selected equals newValue -> return: {}", newValue);
                return;
            }

            component.setSelected(newValue);
        });
    }

    public static void bindToSwing(final ObservableValue<Integer> value, final JSlider component) {
        value.addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(component.getValue(), newValue)) {
                LOGGER.debug("JSlider: Value equals newValue -> return: {}", newValue);
                return;
            }

            component.setValue(newValue);
        });
    }

    public static void bindToSwing(final ObservableValue<String> value, final JLabel component) {
        value.addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(component.getText(), newValue)) {
                LOGGER.debug("JLabel: Text equals newValue -> return: {}", newValue);
                return;
            }

            component.setText(newValue);
        });
    }

    public static void bindToSwing(final ObservableValue<String> value, final JTextComponent component) {
        value.addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(component.getText(), newValue)) {
                LOGGER.debug("JTextComponent: Text equals newValue -> return: {}", newValue);
                return;
            }

            component.setText(newValue);
        });
    }

    public static void bindToSwing(final ObservableValue<String> value, final TitledBorder component) {
        value.addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(component.getTitle(), newValue)) {
                LOGGER.debug("TitledBorder: Title equals newValue -> return: {}", newValue);
                return;
            }

            component.setTitle(newValue);
        });
    }

    public static <T> void bindToSwing(final ObservableValue<T> value, final JComboBox<T> component) {
        value.addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(component.getSelectedItem(), newValue)) {
                LOGGER.debug("JComboBox: SelectedItem equals newValue -> return: {}", newValue);
                return;
            }

            component.setSelectedItem(newValue);
        });
    }

    public static <T> void bindToSwing(final ObservableValue<T> value, final JSpinner component) {
        value.addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(component.getValue(), newValue)) {
                LOGGER.debug("JSpinner: Value equals newValue -> return: {}", newValue);
                return;
            }

            component.setValue(newValue);
        });
    }

    private static <T> void updateProperty(final Property<T> property, final T newValue) {
        Runnable task = () -> {
            if (Objects.equals(property.getValue(), newValue)) {
                LOGGER.debug("newValue equals property -> return: {}", newValue);
                return;
            }

            LOGGER.debug("set newValue in property: {}", newValue);

            property.setValue(newValue);
        };

        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        }
        else {
            SwingUtilities.invokeLater(task);
        }

        // if (Platform.isFxApplicationThread())
        // {
        // task.run();
        // }
        // else
        // {
        // try
        // {
        // Platform.runLater(task);
        // }
        // catch (IllegalStateException ex)
        // {
        // // Toolkit not initialized
        // task.run();
        // }
        // }
    }

    private SwingBindings() {
        super();
    }
}
