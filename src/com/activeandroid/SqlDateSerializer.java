package com.activeandroid;

import java.sql.Date;

final class SqlDateSerializer extends TypeSerializer {
	@Override
	public Class<?> getDeserializedType() {
		return Date.class;
	}

	@Override
	public SerializedType getSerializedType() {
		return SerializedType.LONG;
	}

	@Override
	public Object serialize(Object data) {
		if (data == null) {
			return null;
		}

		return ((Date) data).getTime();
	}

	@Override
	public Date deserialize(Object data) {
		if (data == null) {
			return null;
		}

		return new Date((Long) data);
	}
}