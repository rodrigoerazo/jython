// Copyright � Corporation for National Research Initiatives
package org.python.core;


class SeqFuncs extends PyBuiltinFunctionSet 
{
    SeqFuncs(String name, int index, int argcount) {
        super(name, index, argcount, argcount, true, null);
    }

    public PyObject __call__() {
        PySequence seq = (PySequence)__self__;
        switch (index) {
        case 1:
            return new PyInteger(seq.__nonzero__() ? 1 : 0);
        default:
            throw argCountError(0);
        }
    }

    public PyObject __call__(PyObject arg) {
        PySequence seq = (PySequence)__self__;
        switch (index) {
        case 11:
            return seq.__getitem__(arg);
        case 12:
            seq.__delitem__(arg);
            return Py.None;
        case 13:
            return seq.__mul__(arg);
        case 14:
            return seq.__rmul__(arg);
        case 15:
            return new PyInteger(seq.__cmp__(arg));
        default:
            throw argCountError(1);
        }
    }

    public PyObject __call__(PyObject arg1, PyObject arg2) {
        PySequence seq = (PySequence)__self__;
        switch (index) {
        case 21:
            seq.__setitem__(arg1, arg2);
            return Py.None;
        default:
            throw argCountError(1);
        }
    }

    public PyObject __call__(PyObject arg1, PyObject arg2, PyObject arg3) {
        PySequence seq = (PySequence)__self__;
        switch (index) {
        case 31:
            return seq.__getslice__(arg1, arg2, arg3);
        case 32:
            seq.__delslice__(arg1, arg2, arg3);
            return Py.None;
        default:
            throw argCountError(3);
        }
    }

    public PyObject __call__(PyObject arg1, PyObject arg2,
                             PyObject arg3, PyObject arg4)
    {
        PySequence seq = (PySequence)__self__;
        switch (index) {
        case 41:
            seq.__setslice__(arg1, arg2, arg3, arg4);
            return Py.None;
        default:
            throw argCountError(4);
        }
    }
}



/**
 * The abstract superclass of PyObjects that implements a Sequence.
 * Minimize the work in creating such objects.
 *
 * Method names are designed to make it possible for PySequence to
 * implement java.util.List interface when JDK 1.2 is ubiquitous.
 * 
 * All subclasses must declare that they implement the InitModule
 * interface, and provide an initModule() method that calls
 * PySequence.initModule().
 *
 * Subclasses must also implement get, getslice, and repeat methods.
 *
 * Subclasses that are mutable should also implement: set, setslice, del,
 * and delRange.
 */

// this class doesn't "implement InitModule" because otherwise
// PyJavaClass.init() would try to instantiate it.  That fails because this
// class is abstract.  TBD: is there a way to test for whether a class is
// abstract?
abstract public class PySequence extends PyObject
{
    /**
     * This constructor is used by PyJavaClass.init()
     */
    public PySequence() {}

    /**
     * This constructor is used to pass on an __class__ attribute.
     */
    public PySequence(PyClass c) {
        super(c);
    }

    public void initModule(PyObject dict) {
        dict.__setitem__("__nonzero__", new SeqFuncs("__nonzero__", 1, 0));
        dict.__setitem__("__getitem__", new SeqFuncs("__getitem__", 11, 1));
        dict.__setitem__("__delitem__", new SeqFuncs("__delitem__", 12, 1));
        dict.__setitem__("__mul__", new SeqFuncs("__mul__", 13, 1));
        dict.__setitem__("__rmul__", new SeqFuncs("__rmul__", 14, 1));
        dict.__setitem__("__cmp__", new SeqFuncs("__cmp__", 15, 1));
        dict.__setitem__("__setitem__", new SeqFuncs("__setitem__", 21, 2));
        dict.__setitem__("__getslice__", new SeqFuncs("__getslice__", 31, 3));
        dict.__setitem__("__delslice__", new SeqFuncs("__delslice__", 32, 3));
        dict.__setitem__("__setslice__", new SeqFuncs("__setslice__", 41, 4));
        // TBD: __tojava__()
        // hide these from Python!
        dict.__setitem__("initModule", null);
    }

    // These methods must be defined for any sequence
        
    /**
     * @param index index of element to return.
     * @return the element at the given position in the list.
     */
    abstract protected PyObject get(int index);
        
    /**
     * Returns a range of elements from the sequence.
     * 
     * @param start the position of the first element.
     * @param stop one more than the position of the last element.
     * @param step the step size.
     * @return a sequence corresponding the the given range of elements.
     */
    abstract protected PyObject getslice(int start, int stop, int step);
        
    /**
     * Repeats the given sequence.
     * 
     * @param count the number of times to repeat the sequence.
     * @return this sequence repeated count times.
     */
    abstract protected PyObject repeat(int count);

    // These methods only apply to mutable sequences

    /**
     * Sets the given element of the sequence.
     * 
     * @param index index of the element to set.
     * @param value the value to set this element to.
     */
    protected void set(int index, PyObject value) {
        throw Py.TypeError("can't assign to immutable object");
    }
        
    /**
     * Sets the given range of elements.
     */
    protected void setslice(int start, int stop, int step, PyObject value) {
        throw Py.TypeError("can't assign to immutable object");
    }
        
    protected void del(int i) throws PyException {
        throw Py.TypeError("can't remove from immutable object");
    }
    protected void delRange(int start, int stop, int step) {
        throw Py.TypeError("can't remove from immutable object");
    }   

    public boolean __nonzero__() {
        return __len__() != 0;
    }

    public synchronized int __cmp__(PyObject ob_other) {
        if (ob_other.__class__ != __class__)
            return -2;
                
        PySequence other = (PySequence)ob_other;
        int s1 = __len__();
        int s2 = other.__len__();
        int len = (s1 > s2) ? s2 : s1;

        for (int i=0; i<len; i++) {
            int c = get(i)._cmp(other.get(i));
            if (c != 0)
                return c;
        }
        return s1 < s2 ? -1 : (s1 > s2 ? 1: 0);
    }

    protected static final int sliceLength(int start, int stop, int step) {
        //System.err.println("slice: "+start+", "+stop+", "+step);
        int ret;
        if (step > 0) {
            ret = (stop-start+step-1)/step;
        } else {
            ret = (stop-start+step+1)/step;
        }
        if (ret < 0) return 0;
        return ret;
    }

    private static final int getIndex(PyObject index, int defaultValue) {
        if (index == Py.None || index == null)
            return defaultValue;
        if (!(index instanceof PyInteger))
            throw Py.TypeError("slice index must be int");
        return ((PyInteger)index).getValue();
    }

    protected int fixindex(int index) {
        int l = __len__();
        if (index < 0)
            index += l;
        if (index < 0 || index >= l)
            return -1;
        //throw Py.IndexError("index out of range");
        else return index;
    }

    public synchronized PyObject __finditem__(int index) {
        index = fixindex(index);
        if (index == -1)
            return null;
        else
            return get(index);
    }

    public PyObject __finditem__(PyObject index) {
        if (index instanceof PyInteger)
            return __finditem__(((PyInteger)index).getValue());
        else if (index instanceof PySlice) {
            PySlice s = (PySlice)index;
            return __getslice__(s.start, s.stop, s.step);
        }
        else
            throw Py.TypeError("sequence subscript must be integer or slice");
    }

    public PyObject __getitem__(PyObject index) {
        PyObject ret = __finditem__(index);
        if (ret == null) {
            throw Py.IndexError("index out of range: "+index);
        }
        return ret;
    }

    public boolean isMappingType() { return false; }
    public boolean isNumberType() { return false; }

    protected static final int getStep(PyObject s_step) {
        int step = getIndex(s_step, 1);
        if (step == 0) {
            throw Py.TypeError("slice step of zero not allowed");
        }
        return step;
    }

    protected static final int getStart(PyObject s_start, int step, int length)
    {
        int start;
        if (step < 0)
            start = getIndex(s_start, length-1);
        else
            start = getIndex(s_start, 0);

        if (start < 0) {
            start += length;
            if (start < 0)
                start = 0;
        }
        if (start > length)
            start = length;

        return start;
    }

    protected static final int getStop(PyObject s_stop, int start, int step,
                                       int length)
    {
        int stop;
        if (step < 0) {
            stop = getIndex(s_stop, -1);
            if (stop < -1) stop = length+stop;
            if (stop < -1) stop = -1;
        } else {
            stop = getIndex(s_stop, length);
            if (stop < 0) stop = length+stop;
            if (stop < 0) stop = 0;
        }
        if (stop > length) stop = length;

        return stop;
    }

    public synchronized PyObject __getslice__(PyObject s_start,
                                              PyObject s_stop,
                                              PyObject s_step)
    {
        int length = __len__();
        int step = getStep(s_step);
        int start = getStart(s_start, step, length);
        int stop = getStop(s_stop, start, step, length);
        return getslice(start, stop, step);
    }
    
    public synchronized void __setslice__(PyObject s_start, PyObject s_stop,
                                          PyObject s_step, PyObject value)
    {
        int length = __len__();
        int step = getStep(s_step);
        int start = getStart(s_start, step, length);
        int stop = getStop(s_stop, start, step, length);
        setslice(start, stop, step, value);
    }
    
    public synchronized void __delslice__(PyObject s_start, PyObject s_stop,
                                          PyObject s_step)
    {
        int length = __len__();
        int step = getStep(s_step);
        int start = getStart(s_start, step, length);
        int stop = getStop(s_stop, start, step, length);
        delRange(start, stop, step);
    }

    public synchronized void __setitem__(int index, PyObject value) {
        int i = fixindex(index);
        if (i == -1)
            throw Py.IndexError("index out of range: "+i);
        set(i, value);
    }

    public void __setitem__(PyObject index, PyObject value) {
        if (index instanceof PyInteger)
            __setitem__(((PyInteger)index).getValue(), value);
        else {
            if (index instanceof PySlice) {
                PySlice s = (PySlice)index;
                __setslice__(s.start, s.stop, s.step, value);
            } else {
                throw Py.TypeError(
                    "sequence subscript must be integer or slice");
            }
        }
    }

    public synchronized void __delitem__(PyObject index) {
        if (index instanceof PyInteger) {
            int i = fixindex(((PyInteger)index).getValue());
            if (i == -1)
                throw Py.IndexError("index out of range: "+i);
            del(i);
        }
        else {
            if (index instanceof PySlice) {
                PySlice s = (PySlice)index;
                __delslice__(s.start, s.stop, s.step);
            } else {
                throw Py.TypeError(
                    "sequence subscript must be integer or slice");
            }
        }
    }

    public PyObject __mul__(PyObject count) {
        int repeats = Py.py2int(count, "can't multiply sequence with non-int");
        return repeat(repeats >= 0 ? repeats : 0);
    }

    public PyObject __rmul__(PyObject count) {
        return __mul__(count);
    }

    public synchronized Object __tojava__(Class c) {
        if (c.isArray()) {
            Class component = c.getComponentType();
            //System.out.println("getting: "+component);
            try {
                int n = __len__();
                PyArray array = new PyArray(component, n);
                for (int i=0; i<n; i++) {
                    PyObject o = get(i);
                    array.set(i, o);
                }
                //System.out.println("getting: "+component+", "+array.data);
                return array.data;
            } catch (Throwable t) {
                ;//System.out.println("failed to get: "+component.getName());
            }
        }
        return super.__tojava__(c);
    }
}
