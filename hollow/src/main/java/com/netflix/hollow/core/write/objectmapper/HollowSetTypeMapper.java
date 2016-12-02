/*
 *
 *  Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.core.write.objectmapper;

import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;

import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

public class HollowSetTypeMapper extends HollowTypeMapper {

    private final HollowSetSchema schema;
    private final HollowSetTypeWriteState writeState;

    private final HollowObjectHashCodeFinder hashCodeFinder;

    private final HollowTypeMapper elementMapper;

    public HollowSetTypeMapper(HollowObjectMapper parentMapper, ParameterizedType type, String declaredName, String[] hashKeyFieldPaths, HollowWriteStateEngine stateEngine, boolean useDefaultHashKeys) {
        this.elementMapper = parentMapper.getTypeMapper(type.getActualTypeArguments()[0], null, null);
        String typeName = declaredName != null ? declaredName : getDefaultTypeName(type);
        
        if(hashKeyFieldPaths == null && useDefaultHashKeys && (elementMapper instanceof HollowObjectTypeMapper))
            hashKeyFieldPaths = ((HollowObjectTypeMapper)elementMapper).getDefaultElementHashKey();
        
        this.schema = new HollowSetSchema(typeName, elementMapper.getTypeName(), hashKeyFieldPaths);
        this.hashCodeFinder = stateEngine.getHashCodeFinder();

        HollowSetTypeWriteState existingTypeState = (HollowSetTypeWriteState) parentMapper.getStateEngine().getTypeState(typeName);
        this.writeState = existingTypeState != null ? existingTypeState : new HollowSetTypeWriteState(schema);
    }

    @Override
    protected String getTypeName() {
        return schema.getName();
    }

    @Override
    protected int write(Object obj) {
        Set<?> s = (Set<?>)obj;

        HollowSetWriteRecord rec = (HollowSetWriteRecord)writeRecord();
        for(Object o : s) {
            int ordinal = elementMapper.write(o);
            int hashCode = hashCodeFinder.hashCode(elementMapper.getTypeName(), ordinal, o);
            rec.addElement(ordinal, hashCode);
        }

        return writeState.add(rec);
    }

    @Override
    protected HollowWriteRecord newWriteRecord() {
        return new HollowSetWriteRecord();
    }

    @Override
    protected HollowTypeWriteState getTypeWriteState() {
        return writeState;
    }

}
