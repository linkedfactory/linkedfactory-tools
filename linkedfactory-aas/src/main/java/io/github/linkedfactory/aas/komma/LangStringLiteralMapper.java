package io.github.linkedfactory.aas.komma;

import io.adminshell.aas.v3.model.LangString;
import net.enilink.komma.core.ILiteral;
import net.enilink.komma.core.ILiteralMapper;
import net.enilink.komma.core.Literal;
import net.enilink.komma.core.URI;
import net.enilink.vocab.rdf.RDF;
import net.enilink.vocab.xmlschema.XMLSCHEMA;

/**
 * Supports AAS specific {@link LangString}s.
 */
public class LangStringLiteralMapper implements ILiteralMapper {
	@Override
	public URI getDatatype() {
		return RDF.TYPE_LANGSTRING;
	}

	@Override
	public void setDatatype(URI uri) {
		// do nothing
	}

	@Override
	public Object deserialize(ILiteral literal) {
		return new LangString(literal.getLabel(), literal.getLanguage());
	}

	@Override
	public ILiteral serialize(Object o) {
		LangString ls = (LangString)o;
		if (ls.getLanguage() == null) {
			return new Literal(ls.getValue());
		} else {
			return new Literal(ls.getValue(), ls.getLanguage());
		}
	}
}
