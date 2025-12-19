/*
 * ORIGINAL COPYRIGHT:
 * Copyright (c) 2019-2023 AlphaWallet
 * Licensed under the MIT License (MIT).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * ----------------------------------------------------------------
 * SOURCE:
 * Derived from: https://github.com/AlphaWallet/alpha-wallet-android
 *
 * ----------------------------------------------------------------
 * MODIFICATIONS:
 * Modified by Mangala Wallet for Kotlin Multiplatform compatibility.
 * ----------------------------------------------------------------
 */

package com.alphawallet.token.tools;

import com.alphawallet.token.entity.XMLDsigVerificationResult;
import com.github.cliftonlabs.json_simple.JsonObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class VerifyXMLDSig {

    //Invoke with Lambda via VerifyXMLDSig interface
//    public Response VerifyTSMLFile(Request req) throws Exception {
//        JsonObject result = validateSSLCertificate(req.file);
//        return new Response(result);
//    }
//
//    public JsonObject validateSSLCertificate(String file) throws UnsupportedEncodingException {
//        JsonObject result = new JsonObject();
//        InputStream stream = new ByteArrayInputStream(file.getBytes("UTF-8"));
//        XMLDsigVerificationResult XMLDsigVerificationResult = new XMLDSigVerifier().VerifyXMLDSig(stream);
//        if (XMLDsigVerificationResult.isValid)
//        {
//            result.put("result", "pass");
//            result.put("issuer", XMLDsigVerificationResult.issuerPrincipal);
//            result.put("subject", XMLDsigVerificationResult.subjectPrincipal);
//            result.put("keyName", XMLDsigVerificationResult.keyName);
//            result.put("keyType", XMLDsigVerificationResult.keyType);
//        }
//        else
//        {
//            result.put("result", "fail");
//            result.put("failureReason", XMLDsigVerificationResult.failureReason);
//        }
//        return result;
//    }

    public static class Request {
        String file;

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public Request(String file) {
            this.file = file;
        }

        public Request() {
        }
    }

    public static class Response {
        JsonObject result;

        public JsonObject getResult() { return result; }

        public void setResult(JsonObject result) { this.result = result; }

        public Response(JsonObject result) {
            this.result = result;
        }

        public Response() {
        }
    }

}
