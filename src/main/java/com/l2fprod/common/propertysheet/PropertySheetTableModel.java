/*
 * Copyright 2015 Matthew Aguirre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.l2fprod.common.propertysheet;

import com.l2fprod.common.swing.ObjectTableModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

/**
 * PropertySheetTableModel. <br>
 *
 */
public class PropertySheetTableModel
        extends AbstractTableModel
        implements PropertyChangeListener, PropertySheet, ObjectTableModel {

    public static final int NAME_COLUMN = 0;
    public static final int VALUE_COLUMN = 1;
    public static final int NUM_COLUMNS = 2;

    private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private List<Item> model;
    private List<Item> publishedModel;
    private List<Property> properties;
    private int mode;
    private boolean sortingCategories;
    private boolean sortingProperties;
    private boolean restoreToggleStates;
    private Comparator categorySortingComparator;
    private Comparator propertySortingComparator;
    private Map<String, Boolean> toggleStates;

    public PropertySheetTableModel() {
        model = new ArrayList<Item>();
        publishedModel = new ArrayList<Item>();
        properties = new ArrayList<Property>();
        mode = PropertySheet.VIEW_AS_FLAT_LIST;
        sortingCategories = false;
        sortingProperties = false;
        restoreToggleStates = false;
        toggleStates = new HashMap<String, Boolean>();
    }

    /* (non-Javadoc)
     * @see com.l2fprod.common.propertysheet.PropertySheet#setProperties(com.l2fprod.common.propertysheet.Property[])
     */
    @Override
    public void setProperties(Property[] newProperties) {
        // unregister the listeners from previous properties
        for (Property prop : properties) {
            prop.removePropertyChangeListener(this);
        }

        // replace the current properties
        properties.clear();
        properties.addAll(Arrays.asList(newProperties));

        // add listeners
        for (Property prop : properties) {
            prop.addPropertyChangeListener(this);
        }

        buildModel();
    }

    /* (non-Javadoc)
     * @see com.l2fprod.common.propertysheet.PropertySheet#getProperties()
     */
    @Override
    public Property[] getProperties() {
        return (Property[]) properties.toArray(new Property[properties.size()]);
    }

    /* (non-Javadoc)
     * @see com.l2fprod.common.propertysheet.PropertySheet#addProperty(com.l2fprod.common.propertysheet.Property)
     */
    @Override
    public void addProperty(Property property) {
        properties.add(property);
        property.addPropertyChangeListener(this);
        buildModel();
    }

    /* (non-Javadoc)
     * @see com.l2fprod.common.propertysheet.PropertySheet#addProperty(int, com.l2fprod.common.propertysheet.Property)
     */
    @Override
    public void addProperty(int index, Property property) {
        properties.add(index, property);
        property.addPropertyChangeListener(this);
        buildModel();
    }

    /* (non-Javadoc)
     * @see com.l2fprod.common.propertysheet.PropertySheet#removeProperty(com.l2fprod.common.propertysheet.Property)
     */
    @Override
    public void removeProperty(Property property) {
        properties.remove(property);
        property.removePropertyChangeListener(this);
        buildModel();
    }

    /* (non-Javadoc)
     * @see com.l2fprod.common.propertysheet.PropertySheet#getPropertyCount()
     */
    @Override
    public int getPropertyCount() {
        return properties.size();
    }

    /* (non-Javadoc)
     * @see com.l2fprod.common.propertysheet.PropertySheet#propertyIterator()
     */
    @Override
    public Iterator propertyIterator() {
        return properties.iterator();
    }

    /**
     * Set the current mode, either {@link PropertySheet#VIEW_AS_CATEGORIES} or
     * {@link PropertySheet#VIEW_AS_FLAT_LIST}.
     *
     * @param mode
     */
    public void setMode(int mode) {
        if (this.mode == mode) {
            return;
        }
        this.mode = mode;
        buildModel();
    }

    /**
     * Get the current mode, either {@link PropertySheet#VIEW_AS_CATEGORIES} or
     * {@link PropertySheet#VIEW_AS_FLAT_LIST}.
     *
     * @return
     */
    public int getMode() {
        return mode;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return super.getColumnClass(columnIndex);
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return NUM_COLUMNS;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        return publishedModel.size();
    }

    /* (non-Javadoc)
     * @see com.l2fprod.common.swing.ObjectTableModel#getObject(int)
     */
    @Override
    public Object getObject(int rowIndex) {
        return getPropertySheetElement(rowIndex);
    }

    /**
     * Get the current property sheet element, of type {@link Item}, at the
     * specified row.
     *
     * @param rowIndex
     * @return
     */
    public Item getPropertySheetElement(int rowIndex) {
        return publishedModel.get(rowIndex);
    }

    /**
     * Get whether this model is currently sorting categories.
     *
     * @return
     */
    public boolean isSortingCategories() {
        return sortingCategories;
    }

    /**
     * Set whether this model is currently sorting categories. If this changes
     * the sorting, the model will be rebuilt.
     *
     * @param value
     */
    public void setSortingCategories(boolean value) {
        boolean old = sortingCategories;
        sortingCategories = value;
        if (sortingCategories != old) {
            buildModel();
        }
    }

    /**
     * Get whether this model is currently sorting properties.
     *
     * @return
     */
    public boolean isSortingProperties() {
        return sortingProperties;
    }

    /**
     * Set whether this model is currently sorting properties. If this changes
     * the sorting, the model will be rebuilt.
     *
     * @param value
     */
    public void setSortingProperties(boolean value) {
        boolean old = sortingProperties;
        sortingProperties = value;
        if (sortingProperties != old) {
            buildModel();
        }
    }

    /**
     * Set the comparator used for sorting categories. If this changes the
     * comparator, the model will be rebuilt.
     *
     * @param comp
     */
    public void setCategorySortingComparator(Comparator comp) {
        Comparator old = categorySortingComparator;
        categorySortingComparator = comp;
        if (categorySortingComparator != old) {
            buildModel();
        }
    }

    /**
     * Set the comparator used for sorting properties. If this changes the
     * comparator, the model will be rebuilt.
     *
     * @param comp
     */
    public void setPropertySortingComparator(Comparator comp) {
        Comparator old = propertySortingComparator;
        propertySortingComparator = comp;
        if (propertySortingComparator != old) {
            buildModel();
        }
    }

    /**
     * Set whether or not this model will restore the toggle states when new
     * properties are applied.
     *
     * @param value
     */
    public void setRestoreToggleStates(boolean value) {
        restoreToggleStates = value;
        if (!restoreToggleStates) {
            toggleStates.clear();
        }
    }

    /**
     * Get whether this model is restoring toggle states.
     *
     * @return
     */
    public boolean isRestoreToggleStates() {
        return restoreToggleStates;
    }

    /**
     * @return the category view toggle states.
     */
    public Map getToggleStates() {
        // Call visibilityChanged to populate the toggleStates map
        visibilityChanged(restoreToggleStates);
        return toggleStates;
    }

    /**
     * Sets the toggle states for the category views. Note this <b>MUST</b> be
     * called <b>BEFORE</b> setting any properties.
     *
     * @param toggleStates the toggle states as returned by getToggleStates
     */
    public void setToggleStates(Map toggleStates) {
        // We are providing a toggleStates map - so by definition we must want to
        // store the toggle states
        setRestoreToggleStates(true);
        this.toggleStates.clear();
        this.toggleStates.putAll(toggleStates);
    }

    /**
     * Retrieve the value at the specified row and column location. When the row
     * contains a category or the column is {@link #NAME_COLUMN}, an
     * {@link Item} object will be returned. If the row is a property and the
     * column is {@link #VALUE_COLUMN}, the value of the property will be
     * returned.
     *
     * @param rowIndex
     * @param columnIndex
     * @return
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result = null;
        Item item = getPropertySheetElement(rowIndex);

        if (item.isProperty()) {
            switch (columnIndex) {
                case NAME_COLUMN:
                    result = item;
                    break;

                case VALUE_COLUMN:
                    try {
                        result = item.getProperty().getValue();
                    } catch (Exception e) {
                        Logger.getLogger(PropertySheetTableModel.class.getName()).log(Level.SEVERE, null, e);
                    }
                    break;

                default:
                // should not happen
            }
        } else {
            result = item;
        }
        return result;
    }

    /**
     * Sets the value at the specified row and column. This will have no effect
     * unless the row is a property and the column is {@link #VALUE_COLUMN}.
     *
     * @param value
     * @param rowIndex
     * @param columnIndex
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Item item = getPropertySheetElement(rowIndex);
        if (item.isProperty()) {
            if (columnIndex == VALUE_COLUMN) {
                try {
                    item.getProperty().setValue(value);
                } catch (Exception e) {
                    Logger.getLogger(PropertySheetTableModel.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }

    /**
     * Add a {@link PropertyChangeListener} to the current model.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.removePropertyChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // forward the event to registered listeners
        listeners.firePropertyChange(evt);
    }

    protected void visibilityChanged(final boolean restoreOldStates) {
        // Store the old visibility states
        if (restoreOldStates) {
            for (Item item : publishedModel) {
                toggleStates.put(item.getKey(), item.isVisible() ? Boolean.TRUE : Boolean.FALSE);
            }
        }
        publishedModel.clear();
        for (Item item : model) {
            Item parent = item.getParent();
            if (restoreOldStates) {
                Boolean oldState = toggleStates.get(item.getKey());
                if (oldState != null) {
                    item.setVisible(oldState);
                }
                if (parent != null) {
                    oldState = toggleStates.get(parent.getKey());
                    if (oldState != null) {
                        parent.setVisible(oldState);
                    }
                }
            }
            if (parent == null || parent.isVisible()) {
                publishedModel.add(item);
            }
        }
    }

    private void buildModel() {
        model.clear();

        if (properties != null && properties.size() > 0) {
            List<Property> sortedProperties = sortProperties(properties);

            switch (mode) {
                case PropertySheet.VIEW_AS_FLAT_LIST:
                    // just add all the properties without categories
                    addPropertiesToModel(sortedProperties, null);
                    break;

                case PropertySheet.VIEW_AS_CATEGORIES:
                    // add properties by category
                    List<String> categories = sortCategories(getPropertyCategories(sortedProperties));

                    for (String category : categories) {
                        Item categoryItem = new Item(category, null);
                        model.add(categoryItem);
                        addPropertiesToModel(
                                sortProperties(getPropertiesForCategory(properties, category)),
                                categoryItem);
                    }
                    break;
                default:
                // should not happen
            }
        }

        visibilityChanged(restoreToggleStates);
        fireTableDataChanged();
    }

    protected List<Property> sortProperties(List localProperties) {
        List<Property> sortedProperties = new ArrayList<Property>(localProperties);
        if (sortingProperties) {
            if (propertySortingComparator == null) {
                // if no comparator was defined by the user, use the default
                propertySortingComparator = new PropertyComparator();
            }
            Collections.sort(sortedProperties, propertySortingComparator);
        }
        return sortedProperties;
    }

    protected List<String> sortCategories(List<String> localCategories) {
        List<String> sortedCategories = new ArrayList<String>(localCategories);
        if (sortingCategories) {
            if (categorySortingComparator == null) {
                // if no comparator was defined by the user, use the default
                categorySortingComparator = PropertyComparator.STRING_COMPARATOR;
            }
            Collections.sort(sortedCategories, categorySortingComparator);
        }
        return sortedCategories;
    }

    protected List<String> getPropertyCategories(List<Property> localProperties) {
        List<String> categories = new ArrayList<String>();
        for (Property property : localProperties) {
            if (!categories.contains(property.getCategory())) {
                categories.add(property.getCategory());
            }
        }
        return categories;
    }

    /**
     * Add the specified properties to the model using the specified parent.
     *
     * @param localProperties the properties to add to the end of the model
     * @param parent the {@link Item} parent of these properties, null if none
     */
    private void addPropertiesToModel(List<Property> localProperties, Item parent) {
        for (Property property : localProperties) {
            Item propertyItem = new Item(property, parent);
            model.add(propertyItem);

            // add any sub-properties
            Property[] subProperties = property.getSubProperties();
            if (subProperties != null && subProperties.length > 0) {
                addPropertiesToModel(Arrays.asList(subProperties), propertyItem);
            }
        }
    }

    /**
     * Convenience method to get all the properties of one category.
     */
    private List<Property> getPropertiesForCategory(List<Property> localProperties, String category) {
        List<Property> categoryProperties = new ArrayList<Property>();
        for (Property property : localProperties) {
            if (property.getCategory() != null && property.getCategory().equals(category)) {
                categoryProperties.add(property);
            }
        }
        return categoryProperties;
    }

    public final class Item {

        private final String name;
        private Property property;
        private final Item parent;
        private boolean hasToggle = true;
        private boolean visible = true;

        private Item(String name, Item parent) {
            this.name = name;
            this.parent = parent;
            // this is not a property but a category, always has toggle
            this.hasToggle = true;
        }

        private Item(Property property, Item parent) {
            this.visible = (property == null);
            this.name = property == null ? "" : property.getDisplayName();
            this.property = property;
            this.parent = parent;

            // properties toggle if there are sub-properties
            Property[] subProperties = property == null ? new Property[]{} : property.getSubProperties();
            hasToggle = subProperties != null && subProperties.length > 0;
        }

        public String getName() {
            return name;
        }

        public boolean isProperty() {
            return property != null;
        }

        public Property getProperty() {
            return property;
        }

        public Item getParent() {
            return parent;
        }

        public int getDepth() {
            int depth = 0;
            if (parent != null) {
                depth = parent.getDepth();
                if (parent.isProperty()) {
                    ++depth;
                }
            }
            return depth;
        }

        public boolean hasToggle() {
            return hasToggle;
        }

        public void toggle() {
            if (hasToggle()) {
                visible = !visible;
                visibilityChanged(false);
                fireTableDataChanged();
            }
        }

        public void setVisible(final boolean visible) {
            this.visible = visible;
        }

        public boolean isVisible() {
            return (parent == null || parent.isVisible()) && (!hasToggle || visible);
        }

        public String getKey() {
            StringBuilder key = new StringBuilder(name);
            Item itemParent = parent;
            while (itemParent != null) {
                key.append(":");
                key.append(itemParent.getName());
                itemParent = itemParent.getParent();
            }
            return key.toString();
        }

    }

    /**
     * The default comparator for Properties. Used if no other comparator is
     * defined.
     */
    public static class PropertyComparator implements Comparator {

        static final Comparator STRING_COMPARATOR
                = new NaturalOrderStringComparator();

        public static class NaturalOrderStringComparator implements Comparator {

            @Override
            public int compare(Object o1, Object o2) {
                String s1 = (String) o1;
                String s2 = (String) o2;
                if (s1 == null) {
                    return s2 == null ? 0 : -1;
                } else if (s2 == null) {
                    return 1;
                } else {
                    return s1.compareTo(s2);
                }
            }
        }

        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof Property && o2 instanceof Property) {
                Property prop1 = (Property) o1;
                Property prop2 = (Property) o2;
                return STRING_COMPARATOR.compare(prop1.getDisplayName() == null ? null : prop1.getDisplayName().toLowerCase(),
                        prop2.getDisplayName() == null ? null : prop2.getDisplayName().toLowerCase());
            } else {
                return 0;
            }
        }
    }
}
