package com.example.studentmanagement.repository;

import com.example.studentmanagement.exception.StudentException;
import com.example.studentmanagement.model.Student;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StudentRepository {

    private static final String INSERT_SQL =
            "INSERT INTO students (full_name, age, email) VALUES (?, ?, ?)";
    private static final String SELECT_ALL_SQL =
            "SELECT id, full_name, age, email FROM students ORDER BY id";
    private static final String SELECT_BY_ID_SQL =
            "SELECT id, full_name, age, email FROM students WHERE id = ?";

    private final DataSource dataSource;

    // Constructor injection: Spring supplies the DataSource bean.
    public StudentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Student student) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setString(1, student.getFullName());   // firstName + lastName -> full_name
            ps.setInt(2, student.getAge());
            ps.setString(3, student.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw translate(e);
        }
    }

    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw translate(e);
        }
        return students;
    }

    public Optional<Student> findById(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw translate(e);
        }
    }

    /** full_name is split on the first space back into firstName / lastName. */
    private Student mapRow(ResultSet rs) throws SQLException {
        String fullName = rs.getString("full_name");
        String[] parts = fullName.split(" ", 2);
        String first = parts[0];
        String last = parts.length > 1 ? parts[1] : "";
        return new Student(rs.getInt("id"), first, last, rs.getString("email"), rs.getInt("age"));
    }

    private StudentException translate(SQLException e) {
        String state = e.getSQLState();
        if ("23505".equals(state)) {
            return new StudentException("A student with this email already exists.", e);
        }
        if ("23514".equals(state)) {
            return new StudentException("Student must be older than 18 years.", e);
        }
        return new StudentException("A database error occurred. Please try again later.", e);
    }
}
