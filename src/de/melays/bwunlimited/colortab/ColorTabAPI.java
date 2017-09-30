package de.melays.bwunlimited.colortab;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ColorTabAPI {
	
	static Map< UUID, String > tabTeam = new HashMap<>();
	
	public static void setTabStyle( Player p, String prefix, String suffix, int priority, Collection< ? extends Player > receivers ) {
		try{
			if( prefix == null ) prefix = "";
			if( suffix == null ) suffix = "";
			
			if( prefix.length() > 16 ) prefix = prefix.substring( 0, 16 );
			if( suffix.length() > 16 ) suffix = suffix.substring( 0, 16 );
			
			try {
				String teamName = priority + p.getName();
				
				if( teamName.length() > 16 ) teamName = teamName.substring( 0, 16 );
				
				Constructor< ? > constructor = getNMSClass( "PacketPlayOutScoreboardTeam" ).getConstructor();
				Object packet = constructor.newInstance();
				
				List< String > contents = new ArrayList<>();
				contents.add( p.getName() );
				
	            try {
	                setField( packet, "a", teamName );
	                setField( packet, "b", teamName );
	                setField( packet, "c", prefix );
	                setField( packet, "d", suffix );
	                setField( packet, "e", "ALWAYS" );
	                setField( packet, "h", 0 );
	                setField( packet, "g", contents );
	            } catch( Exception ex ) {
	                setField( packet, "a", teamName );
	                setField( packet, "b", teamName );
	                setField( packet, "c", prefix );
	                setField( packet, "d", suffix );
	                setField( packet, "e", "ALWAYS" );
	                setField( packet, "i", 0 );
	                setField( packet, "h", contents );
	            }
				for( Player t : receivers ) sendPacket( t, packet );
				tabTeam.put( p.getUniqueId(), teamName);
			} catch ( Exception e ) {
				
			}
		}
		catch ( Exception e ) {
			
		}
	}
	
	public static void clearTabStyle( Player p, Collection< ? extends Player > receivers ) {
		try{
		
			if( !tabTeam.containsKey( p.getUniqueId() ) )
				tabTeam.put( p.getUniqueId(), "nothing" );
			
			String teamName = tabTeam.get( p.getUniqueId() );
			
			try {
				Constructor< ? > constructor = getNMSClass( "PacketPlayOutScoreboardTeam" ).getConstructor();
				Object packet = constructor.newInstance();
	
				List< String > contents = new ArrayList<>();
				contents.add( p.getName() );
				
	            try {
	                setField( packet, "a", teamName );
	                setField( packet, "b", teamName );
	                setField( packet, "e", "ALWAYS" );
	                setField( packet, "h", 1 );
	                setField( packet, "g", contents );
	            } catch( Exception ex ) {
	                setField( packet, "a", teamName );
	                setField( packet, "b", teamName );
	                setField( packet, "e", "ALWAYS" );
	                setField( packet, "i", 1 );
	                setField( packet, "h", contents );
	            }
				for( Player t : receivers ) sendPacket( t, packet );
				tabTeam.put( p.getUniqueId(), teamName );
			} catch ( Exception e ) {
				
			}
		}
		catch ( Exception e ) {
			
		}
	}
	
    public static Field modifiers = getField( Field.class, "modifiers" );

    public static Class< ? > getNMSClass( String name ) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split( "\\." )[ 3 ];
        try {
            return Class.forName( "net.minecraft.server." + version + "." + name );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendPacket( Player to, Object packet ) {
        try {
            Object playerHandle = to.getClass().getMethod( "getHandle" ).invoke( to );
            Object playerConnection = playerHandle.getClass().getField( "playerConnection" ).get( playerHandle );
            playerConnection.getClass().getMethod( "sendPacket", getNMSClass( "Packet" ) ).invoke( playerConnection, packet );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static void setField( Object change, String name, Object to ) throws Exception {
        Field field = change.getClass().getDeclaredField( name );
        field.setAccessible( true );
        field.set( change, to );
        field.setAccessible( false );
    }

    public static Field getField( Class< ? > clazz, String name ) {
        try {
            Field field = clazz.getDeclaredField( name );
            field.setAccessible( true );
            if( Modifier.isFinal( field.getModifiers() ) ) {
                modifiers.set( field, field.getModifiers() & ~Modifier.FINAL );
            }
            return field;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split( "\\." )[ 3 ];
    }
}
