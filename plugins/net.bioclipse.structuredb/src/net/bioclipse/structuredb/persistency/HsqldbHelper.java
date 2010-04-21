/* *****************************************************************************
 * Copyright (c) 2007 - 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.structuredb.persistency;

/**
 * @author jonalv
 */
public class HsqldbHelper {

    public static byte[] bitAnd( byte[] a, byte[] b ) {
        byte[] result = new byte[a.length];
        for ( int i = 0; i < result.length; i++ ) {
            result[i] = (byte) (a[i]&b[i]);
        }
        return result;
    }
}
