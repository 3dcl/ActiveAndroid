package com.activeandroid;

/*
 * Copyright (C) 2010 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;
import android.util.Log;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.util.ReflectionUtils;

public final class TableInfo {
	//////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE MEMBERS
	//////////////////////////////////////////////////////////////////////////////////////

	private final Class<? extends Model> mType;
	private final String mTableName;
	private String mIdDBFieldName = Table.DEFAULT_ID_NAME;

	private final Map<Field, String> mColumnNames = new LinkedHashMap<Field, String>();

	public static int getColumnIndex(List<String> colmnNames, String name){
	    int index = -1;
	    for(int i = 0; i < colmnNames.size(); i++){
	        if(colmnNames.get(i).compareToIgnoreCase(name)== 0){
	            index = i;
	            break;
            }
        }
        return index;
    }

	//////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	//////////////////////////////////////////////////////////////////////////////////////

	public TableInfo(Class<? extends Model> type) {
		mType = type;

		final Table tableAnnotation = type.getAnnotation(Table.class);

        if (tableAnnotation != null) {
			mTableName = tableAnnotation.name();
			mIdDBFieldName = tableAnnotation.id();
		}
		else {
			mTableName = type.getSimpleName();
        }

        // Manually add the id column since it is not declared like the other columns.
        Field idField = getIdField(type);
        mColumnNames.put(idField, mIdDBFieldName);

        List<Field> fields = new LinkedList<Field>(ReflectionUtils.getDeclaredColumnFields(type));
        Collections.reverse(fields);

        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                final Column columnAnnotation = field.getAnnotation(Column.class);
                String columnName = columnAnnotation.name();
                if (TextUtils.isEmpty(columnName)) {
                    columnName = field.getName();
                }

                mColumnNames.put(field, columnName);
            }
        }

	}

	//////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	//////////////////////////////////////////////////////////////////////////////////////

	public Class<? extends Model> getType() {
		return mType;
	}

	public String getTableName() {
		return mTableName;
	}

	public String getIdDBFieldName() {
		return mIdDBFieldName;
	}

	public Collection<Field> getFields() {
		return mColumnNames.keySet();
	}

	public String getColumnName(Field field) {
		return mColumnNames.get(field);
	}


    private Field getIdField(Class<?> type) {
        if (Model.class.isAssignableFrom(type)) {
            try {
                //if(mIdFieldName ==  Table.DEFAULT_ID_NAME){
                    return Model.class.getDeclaredField("mId");
                //}else {
                //    return type.getDeclaredField(mIdFieldName);
                //}
            }
            catch (NoSuchFieldException e) {
                Log.e("Impossible!", e.toString());
            }
        }
        else if (type.getSuperclass() != null) {
            return getIdField(type.getSuperclass());
        }

        return null;
    }

}
