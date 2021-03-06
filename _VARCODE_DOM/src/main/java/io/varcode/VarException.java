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
package io.varcode;

/**
 * Base Exception for {@code VarCode}
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class VarException
	extends RuntimeException
{
	private static final long serialVersionUID = 4495417336149528283L;

	public VarException( String message, Throwable throwable ) 
	{
		super( message, throwable );
	}
		
	public VarException( String message ) 
	{
		super( message );
	}
	
	public VarException( Throwable throwable  ) 
	{
		super( throwable );
	}	
}