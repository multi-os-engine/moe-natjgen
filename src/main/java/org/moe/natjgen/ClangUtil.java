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

package org.moe.natjgen;

import org.clang.c.clang;
import org.clang.enums.CXAvailabilityKind;
import org.clang.enums.CXCursorKind;
import org.clang.enums.CXLinkageKind;
import org.clang.enums.CXTokenKind;
import org.clang.enums.CX_StorageClass;
import org.clang.opaque.CXTranslationUnit;
import org.clang.struct.CXCursor;
import org.clang.struct.CXIdxDeclInfo;
import org.clang.struct.CXPlatformAvailability;
import org.clang.struct.CXSourceLocation;
import org.clang.struct.CXSourceRange;
import org.clang.struct.CXString;
import org.clang.struct.CXToken;
import org.moe.natj.general.ptr.IntPtr;
import org.moe.natj.general.ptr.Ptr;
import org.moe.natj.general.ptr.VoidPtr;
import org.moe.natj.general.ptr.impl.PtrFactory;

import java.io.File;
import java.util.ArrayList;

import static org.clang.c.clang.*;

public final class ClangUtil {
    private ClangUtil() {

    }

    /**
     * Framework extension
     */
    private static final String FRAMEWORK_EXTENSION = ".framework";

    /**
     * Returns the file path of a declaration
     *
     * @param decl declaration info
     * @return file path
     */
    public static String getFilePath(CXIdxDeclInfo decl) {
        final String filePath = getFilePath(decl.cursor());
        try {
            return new File(filePath).getCanonicalPath();
        } catch (Throwable t) {
            return filePath;
        }
    }

    /**
     * Returns the file path of a cursor
     *
     * @param cursor cursor
     * @return file path
     */
    public static String getFilePath(CXCursor cursor) {
        CXSourceLocation location = cursor.getCursorLocation();
        VoidPtr file = location.getFile();
        return clang_getFileName(file).toString();
    }

    /**
     * TODO: doc
     *
     * @param cursor
     * @return
     */
    public static int[] getLocation(CXCursor cursor) {
        IntPtr ptr = PtrFactory.newIntArray(3);
        IntPtr line = ptr;
        IntPtr column = ptr.ofs(1);
        IntPtr offset = ptr.ofs(2);
        cursor.getCursorLocation().getExpansionLocation(null, line, column, offset);
        return new int[] { line.getValue(), column.getValue(), offset.getValue()
        };
    }

    /**
     * TODO: doc
     *
     * @param cursor
     * @return
     */
    public static String getLocationUID(CXCursor cursor) {
        @SuppressWarnings("unchecked") Ptr<VoidPtr> file_ref = (Ptr<VoidPtr>)PtrFactory
                .newPointerPtr(Void.class, 2, 1, true, false);
        IntPtr ptr = PtrFactory.newIntArray(3);
        IntPtr line = ptr;
        IntPtr column = ptr.ofs(1);
        IntPtr offset = ptr.ofs(2);
        cursor.getCursorLocation().getExpansionLocation(file_ref, line, column, offset);
        return clang_getFileName(file_ref.get()).toString() + ":" + line.getValue() + ":" + column.getValue() + ":"
                + offset.getValue();
    }

    /**
     * Get the framework for the given path
     *
     * @param path path
     * @return framework name or null
     */
    public static String getFramework(String path) {
        // TODO: do we need "last index of" in case of:
        // /.../a.framework/.../b.framework/... ?
        String path_comps[] = path.split("/");
        for (String comp : path_comps) {
            if (comp.endsWith(FRAMEWORK_EXTENSION)) {
                String framework = comp.substring(0, comp.length() - FRAMEWORK_EXTENSION.length());
                return framework;
            }
        }

        return null;
    }

    /**
     * Get the framework for the given declaration
     *
     * @param decl declaration info
     * @return framework name or null
     */
    public static String getFramework(CXIdxDeclInfo decl) {
        return getFramework(decl.cursor());
    }

    /**
     * Get the framework for the given cursor
     *
     * @param cursor cursor
     * @return framework name or null
     */
    public static String getFramework(CXCursor cursor) {
        return getFramework(getFilePath(cursor));
    }

    /**
     * Get <code> @optional </code> information at source location
     *
     * @param begin start location in which to search
     * @param end   end location
     * @return true if optional otherwise false
     */
    @Deprecated
    public static boolean isOptional(CXTranslationUnit tu, CXSourceLocation begin, CXSourceLocation end) {
        CXSourceRange range = begin.getRange(end);
        boolean isOptional = false;

        @SuppressWarnings("unchecked") Ptr<Ptr<CXToken>> tokens_ref = (Ptr<Ptr<CXToken>>)PtrFactory
                .newPointerPtr(CXToken.class, 2, 1, true, false);
        IntPtr numTokens = PtrFactory.newIntReference();
        clang_tokenize(tu, range, tokens_ref, numTokens);
        Ptr<CXToken> tokens = tokens_ref.get();

        int num_tokens = numTokens.getValue();

        for (int idx = 0; idx < num_tokens - 1; ++idx) {
            CXToken token = tokens.get(idx);

            if (token.getTokenKind() != CXTokenKind.Punctuation) continue;

            boolean equals = false;
            CXString spelling = token.getTokenSpelling(tu);
            equals = "@".equals(spelling.getCString());
            spelling.dispose();
            if (!equals) continue;

            ++idx;
            token = tokens.get(idx);

            if (token.getTokenKind() != CXTokenKind.Keyword) continue;

            spelling = token.getTokenSpelling(tu);
            String keyword = spelling.getCString();
            spelling.dispose();

            if ("optional".equals(keyword)) isOptional = true;
            else if ("required".equals(keyword)) isOptional = false;
        }

        if (num_tokens > 0) {
            clang_disposeTokens(tu, tokens, num_tokens);
        }

        return isOptional;
    }

    /**
     * Tells whether an element at the given cursor is available or not
     *
     * @param c cursor
     * @return true when available otherwise false
     */
    public static boolean isAvailable(CXCursor c) {
        final int cursorAvailability = c.getCursorAvailability();
        return (cursorAvailability == CXAvailabilityKind.Available) || (cursorAvailability
                == CXAvailabilityKind.Deprecated);
    }

    /**
     * Tells whether the given Objective-C declaration is available or not
     *
     * @param d declaration
     * @return true when available otherwise false
     */
    public static boolean isObjCDeclAvailable(CXIdxDeclInfo d) {
        if (!isAvailable(d.cursor())) return false;
        CXCursor cont = d.semanticContainer().cursor();
        return isAvailable(cont);

    }

    /**
     * Tells whether an element at the given cursor is deprecated or not
     *
     * @param c cursor
     * @param platform Platform to check for deprecation
     */
    public static boolean isDeprecated(CXCursor c, String platform) {
        return getDeprecatedVersion(c, platform) != null;
    }

    /**
     * Returns the Introduced since property
     *
     * @param c cursor
     * @param platform Platform to check for property
     * @return the version since the element exist, otherwise null
     */
    public static String getSinceVersion(CXCursor c, String platform) {
        CXPlatformAvailability[] availabilities = c.getCursorPlatformAvailabilities();
        for (CXPlatformAvailability availability : availabilities) {
            if (!availability.Platform().toString().equalsIgnoreCase(platform))
                continue;
            if (!availability.Introduced().toString().isEmpty()) {
                return availability.Introduced().toString();
            }
        }
        return null;
    }

    /**
     * Returns the Deprecated since property
     *
     * @param c cursor
     * @param platform Platform to check for property
     * @return the version since the element is deprecated, otherwise null
     */
    public static String getDeprecatedVersion(CXCursor c, String platform) {
        CXPlatformAvailability[] availabilities = c.getCursorPlatformAvailabilities();
        for (CXPlatformAvailability availability : availabilities) {
            if (!availability.Platform().toString().equalsIgnoreCase(platform))
                continue;
            if (!availability.Deprecated().toString().isEmpty()) {
                return availability.Deprecated().toString();
            }
        }
        return null;
    }

    /**
     * Returns the Deprecated message property
     *
     * @param c cursor
     * @param platform Platform to check for property
     * @return the deprecated message if the element is deprecated, otherwise null
     */
    public static String getDeprecatedMessage(CXCursor c, String platform) {
        CXPlatformAvailability[] availabilities = c.getCursorPlatformAvailabilities();
        for (CXPlatformAvailability availability : availabilities) {
            if (!availability.Platform().toString().equalsIgnoreCase(platform))
                continue;
            if (!availability.Message().toString().isEmpty()) {
                return availability.Message().toString();
            }
        }
        return null;
    }

    public static String nameForLinkage(int linkage) {
        switch (linkage) {
        case CXLinkageKind.External:
            return "External";
        case CXLinkageKind.Internal:
            return "Internal";
        case CXLinkageKind.Invalid:
            return "Invalid";
        case CXLinkageKind.NoLinkage:
            return "NoLinkage";
        case CXLinkageKind.UniqueExternal:
            return "UniqueExternal";
        default:
            throw new RuntimeException("Unknown linkage kind");
        }
    }

    public static String nameForStorageClass(int storageClass) {
        switch (storageClass) {
        case CX_StorageClass.Invalid:
            return "Invalid";
        case CX_StorageClass.None:
            return "None";
        case CX_StorageClass.Extern:
            return "Extern";
        case CX_StorageClass.Static:
            return "Static";
        case CX_StorageClass.PrivateExtern:
            return "PrivateExtern";
        case CX_StorageClass.OpenCLWorkGroupLocal:
            return "OpenCLWorkGroupLocal";
        case CX_StorageClass.Auto:
            return "Auto";
        case CX_StorageClass.Register:
            return "Register";
        default:
            throw new RuntimeException("Unknown storage class");
        }
    }

    /**
     * Get the list of cursors that point to each parameter of the given function decl.
     */
    public static ArrayList<CXCursor> parseParameters(CXCursor decl) {
        CXTranslationUnit cu = clang.clang_Cursor_getTranslationUnit(decl);

        // Get the range that covers the entire function decl
        CXSourceRange r = clang.clang_getCursorExtent(decl);

        // Tokenize the function decl
        @SuppressWarnings("unchecked") Ptr<Ptr<CXToken>> tokens_ref = (Ptr<Ptr<CXToken>>)PtrFactory
                .newPointerPtr(CXToken.class, 2, 1, true, false);
        IntPtr numTokens = PtrFactory.newIntReference();
        clang.clang_tokenize(cu, r, tokens_ref, numTokens);
        Ptr<CXToken> tokens = tokens_ref.get();

        int num_tokens = numTokens.getValue();

        ArrayList<CXCursor> parameters = new ArrayList<>();

        IntPtr offsetRef = PtrFactory.newIntReference();
        int lastOffset = -1;
        for (int idx = 0; idx < num_tokens - 1; ++idx) {
            CXToken token = tokens.get(idx);

            // Skip token that is covered by current cursor
            CXSourceLocation tl = clang.clang_getTokenLocation(cu, token);
            tl.getExpansionLocation(null, null, null, offsetRef);
            if (offsetRef.getValue() < lastOffset) {
                continue;
            }

            // Get the cursor at current token
            CXCursor c = clang.clang_getCursor(cu, clang.clang_getTokenLocation(cu, token));

            // Check if the current cursor is a parameter decl
            if (c.kind() == CXCursorKind.ParmDecl && clang.clang_equalCursors(c, decl) == 0) {
                parameters.add(c);

                // Save the end offset of current cursor
                CXSourceRange cr = clang.clang_getCursorExtent(c);
                CXSourceLocation end = clang.clang_getRangeEnd(cr);
                end.getExpansionLocation(null, null, null, offsetRef);
                lastOffset = offsetRef.getValue();
            }
        }

        return parameters;
    }

    public static String getComment(CXCursor decl, String platform) {
        String since = getSinceVersion(decl, platform);
        String sinceDeprecated = getDeprecatedVersion(decl, platform);
        String deprecatedMessage = getDeprecatedMessage(decl, platform);
        String returnString = "";
        try {
            CXString s = clang_Cursor_getRawCommentText(decl);
            String rawComment = s == null ? null : s.getCString();
            if (rawComment != null) {
                // Add a extra newline if a comment is available
                if (since != null || sinceDeprecated != null || deprecatedMessage != null) rawComment += "\n";
                returnString = rawComment;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (since != null) returnString += "\nAPI-Since: " + since;
        if (sinceDeprecated != null) returnString += "\nDeprecated-Since: " + sinceDeprecated;
        if (deprecatedMessage != null) returnString += "\nDeprecated-Message: " + deprecatedMessage;
        return returnString.isEmpty() ? null : returnString;
    }
}
