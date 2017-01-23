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
package com.l2fprod.common.swing;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * BeanInfo class for JCollapsiblePane.
 */
public class JCollapsiblePaneBeanInfo extends SimpleBeanInfo {

    /**
     * Description of the Field.
     */
    protected BeanDescriptor bd = new BeanDescriptor(
            com.l2fprod.common.swing.JCollapsiblePane.class);
    /**
     * Description of the Field.
     */
    protected Image iconMono16;
    /**
     * Description of the Field.
     */
    protected Image iconColor16;
    /**
     * Description of the Field.
     */
    protected Image iconMono32;
    /**
     * Description of the Field.
     */
    protected Image iconColor32;

    /**
     * Constructor for the JCollapsiblePaneBeanInfo object.
     *
     * @throws java.beans.IntrospectionException
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public JCollapsiblePaneBeanInfo() throws java.beans.IntrospectionException {
        // setup bean descriptor in constructor.
        bd.setName("JCollapsiblePane");

        bd.setShortDescription("A pane which hides its content with an animation.");

        bd.setValue("isContainer", Boolean.TRUE);
        bd.setValue("containerDelegate", "getContentPane");

        BeanInfo info = Introspector.getBeanInfo(getBeanDescriptor().getBeanClass()
                .getSuperclass());
        String order = info.getBeanDescriptor().getValue("propertyorder") == null ? ""
                : (String) info.getBeanDescriptor().getValue("propertyorder");
        PropertyDescriptor[] pd = getPropertyDescriptors();
        for (int i = 0; i != pd.length; i++) {
            if (!order.contains(pd[i].getName())) {
                order = order + (order.length() == 0 ? "" : ":") + pd[i].getName();
            }
        }
        getBeanDescriptor().setValue("propertyorder", order);
    }

    /**
     * Gets the additionalBeanInfo.
     *
     * @return The additionalBeanInfo value
     */
    @Override
    public BeanInfo[] getAdditionalBeanInfo() {
        ArrayList<BeanInfo> bi = new ArrayList<BeanInfo>();
        BeanInfo[] biarr = null;
        try {
            for (Class<?> cl = com.l2fprod.common.swing.JCollapsiblePane.class
                    .getSuperclass(); !cl.equals(java.awt.Component.class.getSuperclass()); cl = cl
                    .getSuperclass()) {
                bi.add(Introspector.getBeanInfo(cl));
            }
            biarr = bi.toArray(new BeanInfo[]{});
        } catch (Exception e) {
            // Ignore it
        }
        return biarr;
    }

    /**
     * Gets the beanDescriptor.
     *
     * @return The beanDescriptor value
     */
    @Override
    public BeanDescriptor getBeanDescriptor() {
        return bd;
    }

    /**
     * Gets the defaultPropertyIndex.
     *
     * @return The defaultPropertyIndex value
     */
    @Override
    public int getDefaultPropertyIndex() {
        String defName = "";
        if ("".equals(defName)) {
            return -1;
        }
        PropertyDescriptor[] pd = getPropertyDescriptors();
        for (int i = 0; i < pd.length; i++) {
            if (pd[i].getName().equals(defName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the icon.
     *
     * @param type Description of the Parameter
     * @return The icon value
     */
    @Override
    public Image getIcon(int type) {
        if (type == BeanInfo.ICON_COLOR_16x16) {
            return iconColor16;
        }
        if (type == BeanInfo.ICON_MONO_16x16) {
            return iconMono16;
        }
        if (type == BeanInfo.ICON_COLOR_32x32) {
            return iconColor32;
        }
        if (type == BeanInfo.ICON_MONO_32x32) {
            return iconMono32;
        }
        return null;
    }

    /**
     * Gets the Property Descriptors.
     *
     * @return The propertyDescriptors value
     */
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            ArrayList<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();
            PropertyDescriptor descriptor;

            try {
                descriptor = new PropertyDescriptor("animated",
                        com.l2fprod.common.swing.JCollapsiblePane.class);
            } catch (IntrospectionException e) {
                descriptor = new PropertyDescriptor("animated",
                        com.l2fprod.common.swing.JCollapsiblePane.class, "getAnimated", null);
            }

            descriptor.setPreferred(true);

            descriptor.setBound(true);

            descriptors.add(descriptor);
            try {
                descriptor = new PropertyDescriptor("collapsed",
                        com.l2fprod.common.swing.JCollapsiblePane.class);
            } catch (IntrospectionException e) {
                descriptor = new PropertyDescriptor("collapsed",
                        com.l2fprod.common.swing.JCollapsiblePane.class, "getCollapsed", null);
            }

            descriptor.setPreferred(true);

            descriptor.setBound(true);

            descriptors.add(descriptor);

            return (PropertyDescriptor[]) descriptors
                    .toArray(new PropertyDescriptor[descriptors.size()]);
        } catch (Exception e) {
            // do not ignore, bomb politely so use has chance to discover what went
            // wrong...
            // I know that this is suboptimal solution, but swallowing silently is
            // even worse... Propose better solution!
            Logger.getLogger(JCollapsiblePane.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    /**
     * Gets the methodDescriptors attribute ...
     *
     * @return The methodDescriptors value
     */
    @Override
    public MethodDescriptor[] getMethodDescriptors() {
        return new MethodDescriptor[0];
    }

}
