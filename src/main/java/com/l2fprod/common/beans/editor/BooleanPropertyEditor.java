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
package com.l2fprod.common.beans.editor;

import com.l2fprod.common.util.ResourceManager;

/**
 * BooleanPropertyEditor.<br>
 *
 */
public class BooleanPropertyEditor extends ComboBoxPropertyEditor {

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public BooleanPropertyEditor() {
        super();
        Object[] values = new Object[]{
            new Value(Boolean.TRUE, ResourceManager.common().getString("true")),
            new Value(Boolean.FALSE, ResourceManager.common().getString("false"))
        };
        setAvailableValues(values);
    }

}
