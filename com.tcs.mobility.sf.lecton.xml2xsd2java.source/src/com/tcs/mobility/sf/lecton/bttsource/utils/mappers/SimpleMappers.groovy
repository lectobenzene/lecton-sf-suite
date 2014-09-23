package com.tcs.mobility.sf.lecton.bttsource.utils.mappers

class SimpleMappers {

	public SimpleMappers() {
	}

	public String getWrapper(String primitiveType){
		switch(primitiveType){
			case 'byte':
				return 'Byte'
			case 'short':
				return 'Short'
			case 'int':
				return 'Integer'
			case 'long':
				return 'Long'
			case 'float':
				return 'Float'
			case 'double':
				return 'Double'
			case 'char':
				return 'Character'
			case 'boolean':
				return 'Boolean'
			default:
				return primitiveType
		}
	}
}
