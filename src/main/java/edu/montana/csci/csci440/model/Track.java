package edu.montana.csci.csci440.model;

import edu.montana.csci.csci440.util.DB;

import javax.lang.model.element.Name;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.apache.commons.lang.StringUtils.substring;

public class Track extends Model {

    private Long trackId;
    private Long albumId;
    private Long mediaTypeId;
    private Long genreId;
    private String name;
    private Long milliseconds;
    private Long bytes;
    private BigDecimal unitPrice;

    public static final String REDIS_CACHE_KEY = "cs440-tracks-count-cache";

    public Track() {
        mediaTypeId = 1L;
        genreId = 1L;
        milliseconds  = 0L;
        bytes  = 0L;
        unitPrice = new BigDecimal("0");
    }

    public Track(ResultSet results) throws SQLException {
        name = results.getString("Name");
        milliseconds = results.getLong("Milliseconds");
        bytes = results.getLong("Bytes");
        unitPrice = results.getBigDecimal("UnitPrice");
        trackId = results.getLong("TrackId");
        albumId = results.getLong("AlbumId");
        mediaTypeId = results.getLong("MediaTypeId");
        genreId = results.getLong("GenreId");
    }

    public static Track find(long i) {
        try {
            try (Connection connect = DB.connect();
                 PreparedStatement stmt = connect.prepareStatement(
                         "SELECT * FROM tracks WHERE TrackId = ?")) {
                stmt.setLong(1, i);
                ResultSet resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    return new Track(resultSet);
                } else {
                    return null;
                }
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public static Long count() {


        // write the query to count number of tracks
        // SELECT COUNT
        // check the redis cache
        // if there is a value there return it
        // else issue the query
        // save the count to redis
        // return the count


        // TODO - also invalidate cache
        // null it out instead of mutate it
        return 0L;
    }

    public Album getAlbum() {
        return Album.find(albumId);
    }

    public MediaType getMediaType() {
        return null;
    }
    public Genre getGenre() {
        return null;
    }
    public List<Playlist> getPlaylists(Long i){
        try {
            try (Connection connect = DB.connect();
                 PreparedStatement stmt = connect.prepareStatement(
                         "SELECT playlists.Name, playlists.PlaylistId\n" +
                                 "FROM tracks\n" +
                                 "     JOIN playlist_track ON tracks.TrackId = playlist_track.TrackId\n" +
                                 "     JOIN playlists ON playlist_track.PlaylistId = playlists.PlaylistId\n" +
                                 "WHERE tracks.TrackId = ?;")) {
                ArrayList<Playlist> result = new ArrayList<>();
                stmt.setLong(1,i);
                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    result.add(new Playlist(resultSet));
                }
                return result;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

@Override
public boolean create() {
    if (verify()) {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO tracks (Name, Milliseconds, Bytes, UnitPrice, albumId, MediaTypeId, GenreId) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, this.getName());
            stmt.setLong(2, this.getMilliseconds());
            stmt.setLong(3, this.getBytes());
            stmt.setBigDecimal(4, this.getUnitPrice());
            stmt.setLong(5, this.getAlbumId());
            stmt.setLong(6, this.getMediaTypeId());
            stmt.setLong(7, this.getGenreId());
            stmt.executeUpdate();
            trackId = DB.getLastID(conn);
            return true;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    } else {
        return false;
        }
    }

    @Override
    public void delete() {
        try {
            try (Connection connect = DB.connect();
                 PreparedStatement stmt = connect.prepareStatement(
                         "DELETE FROM tracks")) {
                ResultSet resultSet = stmt.executeQuery();
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

    }
    @Override
    public boolean update() {
        if (verify()) {
            try (Connection conn = DB.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE tracks SET Name = ? WHERE TrackId = ?")) {
                stmt.setString(1, this.getName());
                stmt.setLong(2, this.getTrackId());
                int i = stmt.executeUpdate();
                if (i == 1) {
                    return true;
                } else {
                    return false;
                }
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        }
        return false;
    }

    public Long getTrackId() {
        return trackId;
    }
    @Override
    public boolean verify() {
        _errors.clear(); // clear any existing errors
        if (name == null || name.isEmpty()) {
            addError("Name can't be null or blank!");
        }
        if (albumId == null || 0 == (albumId)) {
            addError("albumId can't be null or blank!");
        }
        return !hasErrors();
    }
    public void setTrackId(Long trackId) {
        this.trackId = trackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(Long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public Long getBytes() {
        return bytes;
    }

    public void setBytes(Long bytes) {
        this.bytes = bytes;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public void setAlbum(Album album) {
        albumId = album.getAlbumId();
    }

    public Long getMediaTypeId() {
        return mediaTypeId;
    }

    public void setMediaTypeId(Long mediaTypeId) {
        this.mediaTypeId = mediaTypeId;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public String getArtistName() {


        // TODO implement more efficiently
        //  hint: cache on this model object
        // introduce a field for artist name
        return getAlbum().getArtist().getName();
    }

    public String getAlbumTitle() {
        // TODO implement more efficiently
        //  hint: cache on this model object
        return getAlbum().getTitle();
    }

    public static List<Track> advancedSearch(int page, int count, String search, Integer artistId, Integer albumId,
                                             Integer maxRuntime, Integer minRuntime) {
        try {
            try (Connection connect = DB.connect();
                 PreparedStatement stmt = connect.prepareStatement(
                         "SELECT *\n" +
                                 "FROM tracks\n" +
                                 "JOIN albums ON tracks.AlbumId = albums.AlbumId\n" +
                                 "JOIN artists ON albums.ArtistId = artists.ArtistId\n" +
                                 "WHERE artists.ArtistId = ?\n" +
                                 "AND tracks.name LIKE '%?%'\n" +
                                 "AND albums.AlbumId = ?\n" +
                                 "AND Milliseconds < ? AND Milliseconds > ?\n" +
                                 "LIMIT ? OFFSET ?;")) {
                ArrayList<Track> result = new ArrayList<>();
                stmt.setInt(1, artistId);
                stmt.setString(2, search);
                stmt.setInt(6, count);
                stmt.setInt(7, (page-1)*count);
                int counter = 0;
                if (albumId != null) {
                    stmt.setInt(3, albumId);
                } else {
                    counter ++;
                }
                if (maxRuntime != null) {
                    stmt.setInt(4 -  counter, maxRuntime);
                } else {
                    counter ++;
                }
                if (minRuntime != null) {
                    stmt.setInt(5 -  counter, minRuntime);
                } else {
                    counter ++;
                }
                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    result.add(new Track(resultSet));
                }
                return result;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public static List<Track> search(int page, int count, String orderBy, String search) {
        try {
            try (Connection connect = DB.connect();
                 PreparedStatement stmt = connect.prepareStatement(
                         "SELECT * FROM tracks ORDER BY " + orderBy + "  WHERE tracks.name LIKE '%?%' LIMIT ? OFFSET ?")) {
                ArrayList<Track> result = new ArrayList<>();
                stmt.setString(1, search);
                stmt.setInt(2, count);
                stmt.setInt(3, (page-1)*count);
                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    result.add(new Track(resultSet));
                }
                return result;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public static List<Track> forAlbum(Long albumId) {
        return Collections.emptyList();
    }

    // Sure would be nice if java supported default parameter values
    public static List<Track> all() {
        return all(0, Integer.MAX_VALUE);
    }

    public static List<Track> all(int page, int count) {
        return all(page, count, "TrackId");
    }

    public static List<Track> all(int page, int count, String orderBy) {
        try {
            try (Connection connect = DB.connect();
                 PreparedStatement stmt = connect.prepareStatement(
                         "SELECT * \n" +
                                 "FROM tracks\n" +
                                 "    JOIN albums ON tracks.AlbumId = albums.AlbumId\n" +
                                 "    JOIN artists ON albums.ArtistId = artists.ArtistId\n" +
                                 "ORDER BY "+ orderBy + " \n" +
                                 "LIMIT ? OFFSET ?")) {
                ArrayList<Track> result = new ArrayList<>();
                stmt.setInt(1, count);
                stmt.setInt(2, (page-1)*count);
                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    result.add(new Track(resultSet));
                }
                return result;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }
}