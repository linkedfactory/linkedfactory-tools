package io.github.linkedfactory.aas.komma;

import net.enilink.komma.core.ILiteral;
import net.enilink.komma.core.ILiteralMapper;
import net.enilink.komma.core.Literal;
import net.enilink.komma.core.URI;
import net.enilink.vocab.xmlschema.XMLSCHEMA;

/**
 * Maps enums to string literals.
 */
public class EnumLiteralMapper implements ILiteralMapper {
	final URI dataType;
	final Class<? extends Enum> enumClass;

	EnumLiteralMapper(URI dataType, Class<? extends Enum> enumClass) {
		this.dataType = dataType;
		this.enumClass = enumClass;
	}

	public EnumLiteralMapper(Class<? extends Enum> enumClass) {
		this(XMLSCHEMA.TYPE_STRING, enumClass);
	}

	@Override
	public URI getDatatype() {
		return dataType;
	}

	@Override
	public void setDatatype(URI uri) {
		// do nothing
	}

	@Override
	public Object deserialize(ILiteral literal) {
		return Enum.valueOf(enumClass, literal.getLabel());
	}

	@Override
	public ILiteral serialize(Object o) {
		return new Literal(((Enum)o).toString());
	}
}
