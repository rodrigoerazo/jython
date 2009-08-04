// Autogenerated AST node
package org.python.antlr.ast;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.python.antlr.AST;
import org.python.antlr.PythonTree;
import org.python.antlr.adapter.AstAdapters;
import org.python.antlr.base.excepthandler;
import org.python.antlr.base.expr;
import org.python.antlr.base.mod;
import org.python.antlr.base.slice;
import org.python.antlr.base.stmt;
import org.python.core.ArgParser;
import org.python.core.AstList;
import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyType;
import org.python.expose.ExposedGet;
import org.python.expose.ExposedMethod;
import org.python.expose.ExposedNew;
import org.python.expose.ExposedSet;
import org.python.expose.ExposedType;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

@ExposedType(name = "_ast.ListComp", base = AST.class)
public class ListComp extends expr {
public static final PyType TYPE = PyType.fromClass(ListComp.class);
    private expr elt;
    public expr getInternalElt() {
        return elt;
    }
    @ExposedGet(name = "elt")
    public PyObject getElt() {
        return elt;
    }
    @ExposedSet(name = "elt")
    public void setElt(PyObject elt) {
        this.elt = AstAdapters.py2expr(elt);
    }

    private java.util.List<comprehension> generators;
    public java.util.List<comprehension> getInternalGenerators() {
        return generators;
    }
    @ExposedGet(name = "generators")
    public PyObject getGenerators() {
        return new AstList(generators, AstAdapters.comprehensionAdapter);
    }
    @ExposedSet(name = "generators")
    public void setGenerators(PyObject generators) {
        this.generators = AstAdapters.py2comprehensionList(generators);
    }


    private final static PyString[] fields =
    new PyString[] {new PyString("elt"), new PyString("generators")};
    @ExposedGet(name = "_fields")
    public PyString[] get_fields() { return fields; }

    private final static PyString[] attributes =
    new PyString[] {new PyString("lineno"), new PyString("col_offset")};
    @ExposedGet(name = "_attributes")
    public PyString[] get_attributes() { return attributes; }

    public ListComp(PyType subType) {
        super(subType);
    }
    public ListComp() {
        this(TYPE);
    }
    @ExposedNew
    @ExposedMethod
    public void ListComp___init__(PyObject[] args, String[] keywords) {
        ArgParser ap = new ArgParser("ListComp", args, keywords, new String[]
            {"elt", "generators", "lineno", "col_offset"}, 2, true);
        setElt(ap.getPyObject(0, Py.None));
        setGenerators(ap.getPyObject(1, Py.None));
        int lin = ap.getInt(2, -1);
        if (lin != -1) {
            setLineno(lin);
        }

        int col = ap.getInt(3, -1);
        if (col != -1) {
            setLineno(col);
        }

    }

    public ListComp(PyObject elt, PyObject generators) {
        setElt(elt);
        setGenerators(generators);
    }

    public ListComp(Token token, expr elt, java.util.List<comprehension> generators) {
        super(token);
        this.elt = elt;
        addChild(elt);
        this.generators = generators;
        if (generators == null) {
            this.generators = new ArrayList<comprehension>();
        }
        for(PythonTree t : this.generators) {
            addChild(t);
        }
    }

    public ListComp(Integer ttype, Token token, expr elt, java.util.List<comprehension> generators)
    {
        super(ttype, token);
        this.elt = elt;
        addChild(elt);
        this.generators = generators;
        if (generators == null) {
            this.generators = new ArrayList<comprehension>();
        }
        for(PythonTree t : this.generators) {
            addChild(t);
        }
    }

    public ListComp(PythonTree tree, expr elt, java.util.List<comprehension> generators) {
        super(tree);
        this.elt = elt;
        addChild(elt);
        this.generators = generators;
        if (generators == null) {
            this.generators = new ArrayList<comprehension>();
        }
        for(PythonTree t : this.generators) {
            addChild(t);
        }
    }

    @ExposedGet(name = "repr")
    public String toString() {
        return "ListComp";
    }

    public String toStringTree() {
        StringBuffer sb = new StringBuffer("ListComp(");
        sb.append("elt=");
        sb.append(dumpThis(elt));
        sb.append(",");
        sb.append("generators=");
        sb.append(dumpThis(generators));
        sb.append(",");
        sb.append(")");
        return sb.toString();
    }

    public <R> R accept(VisitorIF<R> visitor) throws Exception {
        return visitor.visitListComp(this);
    }

    public void traverse(VisitorIF<?> visitor) throws Exception {
        if (elt != null)
            elt.accept(visitor);
        if (generators != null) {
            for (PythonTree t : generators) {
                if (t != null)
                    t.accept(visitor);
            }
        }
    }

    private int lineno = -1;
    @ExposedGet(name = "lineno")
    public int getLineno() {
        if (lineno != -1) {
            return lineno;
        }
        return getLine();
    }

    @ExposedSet(name = "lineno")
    public void setLineno(int num) {
        lineno = num;
    }

    private int col_offset = -1;
    @ExposedGet(name = "col_offset")
    public int getCol_offset() {
        if (col_offset != -1) {
            return col_offset;
        }
        return getCharPositionInLine();
    }

    @ExposedSet(name = "col_offset")
    public void setCol_offset(int num) {
        col_offset = num;
    }

}
