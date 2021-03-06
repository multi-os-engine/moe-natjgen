/*
Copyright 2014-2016 Intel Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.moe.natjgen.test.pointers;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class OpaquePointers extends AbstractPointersTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp("moe_opaqueptr_t", "moe_opaqueptr_t", "Ptr<moe_opaqueptr_t>", "ConstPtr<moe_opaqueptr_t>");
    }

    @Override
    protected boolean baseTypeIsPrimitive() {
        return false;
    }

    @Override
    public void test_T_ptr_ptr_fn_ret() {
        MethodDeclaration method = getStaticMethod("" + nativeT + "_ptr_ptr_fn_ret");
        assertNull(method);
    }

    @Override
    public void test_T_ptr_const_ptr_fn_ret() {
        MethodDeclaration method = getStaticMethod("" + nativeT + "_ptr_const_ptr_fn_ret");
        assertNull(method);
    }

    @Override
    public void test_const_T_ptr_ptr_fn_ret() {
        MethodDeclaration method = getStaticMethod("const_" + nativeT + "_ptr_ptr_fn_ret");
        assertNull(method);
    }

    @Override
    public void test_const_T_ptr_const_ptr_fn_ret() {
        MethodDeclaration method = getStaticMethod("const_" + nativeT + "_ptr_const_ptr_fn_ret");
        assertNull(method);
    }

    @Override
    public void test_T_ptr_ptr_ptr_fn_ret() {
        MethodDeclaration method = getStaticMethod("" + nativeT + "_ptr_ptr_ptr_fn_ret");
        assertNull(method);
    }

    @Override
    public void test_T_ptr_ptr_const_ptr_fn_ret() {
        MethodDeclaration method = getStaticMethod("" + nativeT + "_ptr_ptr_const_ptr_fn_ret");
        assertNull(method);
    }

    @Override
    public void test_T_ptr_const_ptr_ptr_fn_ret() {
        MethodDeclaration method = getStaticMethod("" + nativeT + "_ptr_const_ptr_ptr_fn_ret");
        assertNull(method);
    }

    @Override
    public void test_T_ptr_const_ptr_const_ptr_fn_ret() {
        MethodDeclaration method = getStaticMethod("" + nativeT + "_ptr_const_ptr_const_ptr_fn_ret");
        assertNull(method);
    }

    @Override
    public void test_const_T_ptr_ptr_ptr_fn_ret() {
        MethodDeclaration method = getStaticMethod("const_" + nativeT + "_ptr_ptr_ptr_fn_ret");
        assertNull(method);
    }

    @Override
    public void test_const_T_ptr_ptr_const_ptr_fn_ret() {
        MethodDeclaration method = getStaticMethod("const_" + nativeT + "_ptr_ptr_const_ptr_fn_ret");
        assertNull(method);
    }

    @Override
    public void test_const_T_ptr_const_ptr_ptr_fn_ret() {
        MethodDeclaration method = getStaticMethod("const_" + nativeT + "_ptr_const_ptr_ptr_fn_ret");
        assertNull(method);
    }

    @Override
    public void test_const_T_ptr_const_ptr_const_ptr_fn_ret() {
        MethodDeclaration method = getStaticMethod("const_" + nativeT + "_ptr_const_ptr_const_ptr_fn_ret");
        assertNull(method);
    }

    @Override
    public void test_T_ptr_ptr_fn_arg() {
        MethodDeclaration method = getStaticMethod("" + nativeT + "_ptr_ptr_fn_arg");
        assertNull(method);
    }

    @Override
    public void test_T_ptr_const_ptr_fn_arg() {
        MethodDeclaration method = getStaticMethod("" + nativeT + "_ptr_const_ptr_fn_arg");
        assertNull(method);
    }

    @Override
    public void test_const_T_ptr_ptr_fn_arg() {
        MethodDeclaration method = getStaticMethod("const_" + nativeT + "_ptr_ptr_fn_arg");
        assertNull(method);
    }

    @Override
    public void test_const_T_ptr_const_ptr_fn_arg() {
        MethodDeclaration method = getStaticMethod("const_" + nativeT + "_ptr_const_ptr_fn_arg");
        assertNull(method);
    }

    @Override
    public void test_T_ptr_ptr_ptr_fn_arg() {
        MethodDeclaration method = getStaticMethod("" + nativeT + "_ptr_ptr_ptr_fn_arg");
        assertNull(method);
    }

    @Override
    public void test_T_ptr_ptr_const_ptr_fn_arg() {
        MethodDeclaration method = getStaticMethod("" + nativeT + "_ptr_ptr_const_ptr_fn_arg");
        assertNull(method);
    }

    @Override
    public void test_T_ptr_const_ptr_ptr_fn_arg() {
        MethodDeclaration method = getStaticMethod("" + nativeT + "_ptr_const_ptr_ptr_fn_arg");
        assertNull(method);
    }

    @Override
    public void test_T_ptr_const_ptr_const_ptr_fn_arg() {
        MethodDeclaration method = getStaticMethod("" + nativeT + "_ptr_const_ptr_const_ptr_fn_arg");
        assertNull(method);
    }

    @Override
    public void test_const_T_ptr_ptr_ptr_fn_arg() {
        MethodDeclaration method = getStaticMethod("const_" + nativeT + "_ptr_ptr_ptr_fn_arg");
        assertNull(method);
    }

    @Override
    public void test_const_T_ptr_ptr_const_ptr_fn_arg() {
        MethodDeclaration method = getStaticMethod("const_" + nativeT + "_ptr_ptr_const_ptr_fn_arg");
        assertNull(method);
    }

    @Override
    public void test_const_T_ptr_const_ptr_ptr_fn_arg() {
        MethodDeclaration method = getStaticMethod("const_" + nativeT + "_ptr_const_ptr_ptr_fn_arg");
        assertNull(method);
    }

    @Override
    public void test_const_T_ptr_const_ptr_const_ptr_fn_arg() {
        MethodDeclaration method = getStaticMethod("const_" + nativeT + "_ptr_const_ptr_const_ptr_fn_arg");
        assertNull(method);
    }

}
