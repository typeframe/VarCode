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

/** 
 * Mark to "wrap" code which will be "cut" when tailoring the {@code Markup}.
 */
//
// varcode : "/*{-*/LOG.trace("in trace");/*-}*/"
public class Cut
    extends Mark
    implements Mark.RemovesText
{
    private final String hidesContent;

    public Cut( String text, int lineNumber, String hidesContent )
    {
        super( text, lineNumber );
        this.hidesContent = hidesContent;
    }
    
    public String toString()
    {
        return text;
    }

	@Override
	public String getBetweenText() 
	{
		return hidesContent;
	}
}