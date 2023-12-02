package edu.montana.csci.csci440.model;

import edu.montana.csci.csci440.util.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Album extends Model {

    Long albumId;
    Long artistId;
    String title;

    public Album() {
    }

    private Album(ResultSet results) throws SQLException {
        title = results.getString("Title");
        albumId = results.getLong("AlbumId");
        artistId = results.getLong("ArtistId");
    }

    public Artist getArtist() {
        return Artist.find(artistId);
    }

    public void setArtist(Artist artist) {
        artistId = artist.getArtistId();
    }

    public List<Track> getTracks() {
        return Track.forAlbum(albumId);
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbum(Album album) {
        this.albumId = album.getAlbumId();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public Long getArtistId() {
        return artistId;
    }
    @Override
    public boolean verify() {
        _errors.clear(); // clear any existing errors
        if (title == null || title.isEmpty()) {
            addError("Title can't be null or blank!");
        }
        if (artistId == null || 0 == (artistId)) {
            addError("artistId can't be null or blank!");
        }
        return !hasErrors();
    }
    @Override
    public boolean create() {
        if (verify()) {
            try (Connection conn = DB.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO albums (title, artistId) VALUES (?, ?)")) {
                stmt.setString(1, this.getTitle());
                stmt.setLong(2, this.getArtistId());
                stmt.executeUpdate();
                albumId = DB.getLastID(conn);
                return true;
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        } else {
            return false;
        }
    }
    @Override
    public boolean update() {
        if (verify()) {
            try (Connection conn = DB.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE albums SET Title = ? WHERE AlbumId = ?")) {
                stmt.setString(1, this.getTitle());
                stmt.setLong(2, this.getAlbumId());
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

    public static List<Album> all() {
        try (Connection connect = DB.connect();
             PreparedStatement stmt = connect.prepareStatement(
                     "SELECT * FROM Albums")) {
            ArrayList<Album> result = new ArrayList<>();
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                result.add(new Album(resultSet));
            }
            return result;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public static List<Album> all ( int page, int count){
        try {
            try (Connection connect = DB.connect();
                 PreparedStatement stmt = connect.prepareStatement(
                         "SELECT * FROM albums LIMIT ? OFFSET ?")) {
                ArrayList<Album> result = new ArrayList<>();
                stmt.setInt(1, count);
                stmt.setInt(2, (page - 1) * 100);
                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    result.add(new Album(resultSet));
                }
                return result;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public static Album find ( long i){
        try (Connection connect = DB.connect();
             PreparedStatement stmt = connect.prepareStatement(
                     "SELECT * FROM albums WHERE AlbumId = ?")) {
            stmt.setLong(1, i);
            ResultSet resultSet = stmt.executeQuery();
            return new Album(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Album> getForArtist (Long artistId){
        try {
            try (Connection connect = DB.connect();
                 PreparedStatement stmt = connect.prepareStatement(
                         "SELECT * FROM albums JOIN artists ON artists.ArtistId = albums.ArtistId WHERE artists.artistId = ?")) {
                ArrayList<Album> result = new ArrayList<>();
                stmt.setLong(1, artistId);
                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    result.add(new Album(resultSet));
                }
                return result;
            }


        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }
}
