package edu.montana.csci.csci440.homework;

import edu.montana.csci.csci440.DBTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class Homework1 extends DBTest {

    @Test
    /*
     * Write a query in the string below that returns all artists that have an 'A' in their name
     */
    void selectArtistsWhoseNameHasAnAInIt(){
        List<Map<String, Object>> results = executeSQL("SELECT * FROM artists WHERE name LIKE '%a%'");
        assertEquals(211, results.size());
    }

    @Test
    /*
     * Write a query in the string below that returns all artists that have more than one album
     */
    void selectAllArtistsWithMoreThanOneAlbum(){
        List<Map<String, Object>> results = executeSQL(
                "SELECT artists.Name,\n" +
                        "       COUNT(DISTINCT albums.AlbumId) as Albums\n" +
                        "FROM albums\n" +
                        "    JOIN artists ON albums.ArtistId = artists.ArtistId\n" +
                        "GROUP BY albums.ArtistId\n" +
                        "HAVING Albums >= 2");

        assertEquals(56, results.size());
        assertEquals("AC/DC", results.get(0).get("Name"));
    }

    @Test
        /*
         * Write a query in the string below that returns all tracks longer than six minutes along with the
         * album and artist name
         */
    void selectTheTrackAndAlbumAndArtistForAllTracksLongerThanSixMinutes() {
        List<Map<String, Object>> results = executeSQL(
                "SELECT tracks.name, albums.title, artists.name\n" +
                        "FROM tracks\n" +
                        "JOIN albums ON tracks.AlbumId = albums.AlbumId\n" +
                        "JOIN artists ON albums.ArtistId = artists.ArtistId\n" +
                        "WHERE Milliseconds > 6 * 60 * 1000");

        assertEquals(623, results.size());
    }

}
