// Copyright (c) Corporation for National Research Initiatives
package org.python.core;

/**
 * A super class for all python code implementations.
 */
public abstract class PyCode extends PyObject
{
    public String co_name;

    abstract public PyObject call(PyFrame frame, PyObject closure);

    public PyObject call(PyFrame frame) { return call(frame,null); } // commodity

    abstract public PyObject call(PyObject args[], String keywords[],
                                  PyObject globals, PyObject[] defaults, PyObject closure);

    abstract public PyObject call(PyObject self, PyObject args[],
                                  String keywords[],
                                  PyObject globals, PyObject[] defaults,PyObject closure);

    abstract public PyObject call(PyObject globals, PyObject[] defaults, PyObject closure);

    abstract public PyObject call(PyObject arg1, PyObject globals,
                                  PyObject[] defaults, PyObject closure);

    abstract public PyObject call(PyObject arg1, PyObject arg2,
                                  PyObject globals, PyObject[] defaults, PyObject closure);

    abstract public PyObject call(PyObject arg1, PyObject arg2, PyObject arg3,
                                  PyObject globals, PyObject[] defaults, PyObject closure);

}
