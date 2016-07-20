package io.varcode.context.lib.core;

import java.io.ByteArrayInputStream;

import io.varcode.Metadata;
import io.varcode.context.VarContext;
import io.varcode.context.lib.math.SHA1Checksum;
import io.varcode.dom.Dom;
import io.varcode.dom.bindml.BindML;
import io.varcode.tailor.Tailor;
import io.varcode.tailor.TailorState;
import junit.framework.TestCase;

public class SHA1ChecksumTest
	extends TestCase
{
	public void testDomAndTailorChecksum()
	{
		
	}
	public void testChecksum()
	{
		
		String sha1 = SHA1Checksum.INSTANCE.generateChecksum( 
			new ByteArrayInputStream( "ABCDEFGHI".getBytes() ) );
		/*
		String sha256 = Checksum.INSTANCE.generateChecksum( 
			new ByteArrayInputStream( "ABCDEFGHI".getBytes() ), "SHA-256" );
		
		String md5 = Checksum.INSTANCE.generateChecksum( 
			new ByteArrayInputStream( "ABCDEFGHI".getBytes() ), "MD5" );		
	*/
		Dom m = BindML.compile( "{$$checksum()$$}ABCDEFGHI" );
		
		TailorState ts = Tailor.tailor( m , new VarContext() );
		
		//assertEquals( "SHA-1", m.getMetadata().get( SHA1Checksum.CHECKSUM_ALGORITHM_VARNAME) );
		
		Metadata md = ts.getContext().getMetadata();
		assertNotNull ( md );
		System.out.println( md );
		assertEquals( sha1, md.get( SHA1Checksum.TAILOR_CHECKSUM_SHA1_NAME ) );
		
		//m = BindML.compile( "{$$checksum()$$}ABCDEFGHI" );
		//Tailor.code( m , new VarContext() );
		//assertEquals(sha1, m.getMetadata().get( SHA1Checksum.CHECKSUM_VARNAME ) );
		//assertEquals( "SHA-1", m.getMetadata().get( SHA1Checksum.CHECKSUM_ALGORITHM_VARNAME) );
		
		/*
		m = BindML.compile( "{$$checksum(SHA-256)$$}ABCDEFGHI" );
		Tailor.code( m , new VarContext() );
		assertEquals( sha256, m.getMetadata().get( Checksum.CHECKSUM_VARNAME ) );
		assertEquals( "SHA-256", m.getMetadata().get( Checksum.CHECKSUM_ALGORITHM_VARNAME) );
		
		m = BindML.compile( "{$$checksum(MD5)$$}ABCDEFGHI" );
		Tailor.code( m , new VarContext() );
		assertEquals( md5, m.getMetadata().get( Checksum.CHECKSUM_VARNAME ) );
		assertEquals( "MD5", m.getMetadata().get( Checksum.CHECKSUM_ALGORITHM_VARNAME) );
		*/
	}
}
