/*
 * Copyright (c) 2000 jPOS.org.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the jPOS project 
 *    (http://www.jpos.org/)". Alternately, this acknowledgment may 
 *    appear in the software itself, if and wherever such third-party 
 *    acknowledgments normally appear.
 *
 * 4. The names "jPOS" and "jPOS.org" must not be used to endorse 
 *    or promote products derived from this software without prior 
 *    written permission. For written permission, please contact 
 *    license@jpos.org.
 *
 * 5. Products derived from this software may not be called "jPOS",
 *    nor may "jPOS" appear in their name, without prior written
 *    permission of the jPOS project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  
 * IN NO EVENT SHALL THE JPOS PROJECT OR ITS CONTRIBUTORS BE LIABLE FOR 
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS 
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the jPOS Project.  For more
 * information please see <http://www.jpos.org/>.
 */

package org.jpos.iso;
import java.io.InputStream;
import java.io.IOException;


/**
 * @author apr@cs.com.uy
 * @version $Id$
 * @see ISOComponent
 * @see IFB_LLHECHAR
 * @see IF_ECHAR
 */
public class IFB_LLLHECHAR extends ISOFieldPackager {
    public IFB_LLLHECHAR() {
    	super();
    }
    /**
     * @param len - field len
     * @param description symbolic descrption
     */
    public IFB_LLLHECHAR (int len, String description) {
        super(len, description);
    }
    /**
     * @param c - a component
     * @return packed component
     * @exception ISOException
     */
    public byte[] pack (ISOComponent c) throws ISOException {
        int len;
        String s = (String) c.getValue();
    
        if ((len=s.length()) > getLength() || len>255)   // paranoia settings
            throw new ISOException (
                "invalid len "+len +" packing field "+(Integer) c.getKey()
            );

        byte[] b = new byte[len + 2];
        b[0] = (byte) (len >> 8);
        b[1] = (byte) (len & 0xFF);
        System.arraycopy(ISOUtil.asciiToEbcdic(s), 0, b, 2, len);
        return b;
    }
    /**
     * @param c - the Component to unpack
     * @param b - binary image
     * @param offset - starting offset within the binary image
     * @return consumed bytes
     * @exception ISOException
     */
    public int unpack (ISOComponent c, byte[] b, int offset)
        throws ISOException
    {
        int len = ((int) b[offset++] & 0xFF) << 8;
        len |= ((int) b[offset++] & 0xFF);
        len = Math.min (len, getLength ());
        c.setValue(ISOUtil.ebcdicToAscii(b, offset, len));
        return len + 2;
    }
    public void unpack (ISOComponent c, InputStream in) 
        throws IOException, ISOException
    {
        byte[] b = readBytes (in, 2);
        int len = ((int) b[0] & 0xFF) << 8;
        len |= ((int) b[1] & 0xFF);
        len = Math.min (len, getLength ());
        c.setValue (ISOUtil.ebcdicToAscii(readBytes (in, len)));
    }
    public int getMaxPackedLength() {
        return getLength() + 2;
    }
}
