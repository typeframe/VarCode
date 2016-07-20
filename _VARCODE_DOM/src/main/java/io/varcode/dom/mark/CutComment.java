/*
 * Copyright 2015 M. Eric DeFazio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.varcode.dom.mark;

/** Comment in the Template that is not written to the Tailored Document */
//
// varcode : "/*{- cutThis -}*/"
//     MarkPrefix     : "/*{-"
//     MarkPostfix    : "-}*/"
//     WrappedContent : " cutThis "        
public class CutComment 
    extends Mark
    implements Mark.RemovesText
{
    private final String hiddenContent;
    
    public CutComment( String text, int lineNumber, String hiddenContent )
    {
        super( text, lineNumber );
        this.hiddenContent = hiddenContent;
    }
    
    public String getBetweenText()
    {
        return this.hiddenContent;
    }
    
    public String toString()
    {
        return text;
    }
}