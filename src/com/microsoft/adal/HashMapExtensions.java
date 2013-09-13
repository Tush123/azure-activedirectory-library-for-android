/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */

package com.microsoft.adal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringTokenizer;

/**
 * MOVED from Hervey's code
 * A helper class for URLFormEncode and URLFormDecode of HashMap<string,string> objects
 */
final class HashMapExtensions
{
    /**
     * URL form decode a string into a HashMap<String, String>
     */
    static final HashMap<String, String> URLFormDecode( String parameters )
    {
        HashMap<String,String> result = new HashMap<String, String>();
        
        if ( parameters != null && parameters.length() > 0 )
        {
            StringTokenizer parameterTokenizer = new StringTokenizer( parameters, "&" );
            
            while ( parameterTokenizer.hasMoreTokens() )
            {
                String   pair     = parameterTokenizer.nextToken();
                String[] elements = pair.split( "=" );
                
                if ( elements != null && elements.length == 2 )
                {
                    String key   = StringExtensions.URLFormDecode( elements[0].trim() );
                    String value = StringExtensions.URLFormDecode(  elements[1].trim() );
                    
                    if ( key != null && key.length() > 0 && value != null && value.length() > 0 )
                    {
                        result.put( key, value );
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * URL form encode a HashMap<String, String> into a string 
     */
    static final String URLFormEncode( HashMap<String,String> parameters )
    {
        Iterator<Entry<String, String>> iterator = parameters.entrySet().iterator();
        String result = null;
        
        while ( iterator.hasNext() )
        {
            Entry<String, String> entry = iterator.next();
            
            if ( result == null )
            {
                result = String.format( "%s=%s", StringExtensions.URLFormEncode( entry.getKey() ), StringExtensions.URLFormEncode( entry.getValue() ) );
            }
            else
            {
                result = String.format( "%s&%s=%s", result, StringExtensions.URLFormEncode( entry.getKey() ), StringExtensions.URLFormEncode( entry.getValue() ) );
            }
            
        }
        
        return result;
    }
}
