package io.varcode.context.lib.state;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;

/**
 * A Proxy to a Script <CODE>Bindings</CODE> that records 
 * all of the parameter names passed in the contains() and 
 * get() method calls. 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class VarNameAccessRecorder
	implements javax.script.Bindings
{
	public Set<String> varNamesRequested = new HashSet<String>();

	public Bindings bindings;
	
	public Set<String> getVarNamesRequested()
	{
		return varNamesRequested;
	}
	
	public VarNameAccessRecorder( Bindings bindings )
	{
		this.bindings = bindings;
	}
	
	@Override
	public int size() 
	{
		return bindings.size();		
	}

	@Override
	public boolean isEmpty() 
	{
		return bindings.isEmpty();
	}

	@Override
	public boolean containsValue( Object value ) 
	{
		return bindings.containsValue( value );
	}

	@Override
	public void clear() 
	{
		bindings.clear();
	}

	@Override
	public Set<String> keySet() 
	{
		return bindings.keySet();
	}

	@Override
	public Collection<Object> values() 
	{
		return bindings.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() 
	{
		return bindings.entrySet();
	}

	@Override
	public Object put(String name, Object value) 
	{
		return bindings.put( name, value );
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> toMerge) 
	{
		bindings.putAll( toMerge );		
	}

	@Override
	public boolean containsKey( Object key ) 
	{
		varNamesRequested.add( (String)key );
		return bindings.containsKey( key );
	}

	@Override
	public Object get( Object key ) 
	{
		varNamesRequested.add( (String)key );
		return bindings.get( key );		
	}

	@Override
	public Object remove( Object key ) 
	{
		return bindings.remove( key );
	}

}
