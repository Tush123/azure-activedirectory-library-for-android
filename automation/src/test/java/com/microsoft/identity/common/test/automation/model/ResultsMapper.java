//  Copyright (c) Microsoft Corporation.
//  All rights reserved.
//
//  This code is licensed under the MIT License.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files(the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions :
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.

package com.microsoft.identity.common.test.automation.model;


import com.microsoft.identity.common.internal.net.ObjectMapper;

import org.apache.http.util.TextUtils;

public class ResultsMapper {

    public static ReadCacheResult GetReadCacheResultFromString(String results) {
        ReadCacheResult readCacheResult = (ReadCacheResult) ObjectMapper.deserializeJsonStringToObject(results, ReadCacheResult.class);

        if(readCacheResult!=null) {
            for (String result : readCacheResult.items) {
                TokenCacheItemReadResult tokenCacheItemReadResult = (TokenCacheItemReadResult) ObjectMapper.deserializeJsonStringToObject(result, TokenCacheItemReadResult.class);
                readCacheResult.tokenCacheItems.add(tokenCacheItemReadResult);
            }
        }

        return readCacheResult;

    }

    public static ADALErrorResult GetADALErrorResultFromString(String results) {
        ADALErrorResult adalErrorResult = (ADALErrorResult) ObjectMapper.deserializeJsonStringToObject(results, ADALErrorResult.class);
        return adalErrorResult;
    }

    public static AuthenticationResult GetAuthenticationResultFromString(String results) {
        return (AuthenticationResult) ObjectMapper.deserializeJsonStringToObject(results, AuthenticationResult.class);
    }

    public static TokenCacheItemReadResult GetTokenCacheItemReadResult(String results){
        ReadCacheResult readCacheResult = (ReadCacheResult) ObjectMapper.deserializeJsonStringToObject(results, ReadCacheResult.class);
        TokenCacheItemReadResult tokenCacheItemReadResult = null;
        if(readCacheResult!=null) {
            for (String result : readCacheResult.items) {
               tokenCacheItemReadResult = (TokenCacheItemReadResult) ObjectMapper.deserializeJsonStringToObject(result, TokenCacheItemReadResult.class);
                if(!TextUtils.isEmpty(tokenCacheItemReadResult.accessToken)){
                    return tokenCacheItemReadResult;
                }
            }
        }
        return tokenCacheItemReadResult;
    }
}
