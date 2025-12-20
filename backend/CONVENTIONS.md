# Backend Conventions

> Java 21 + Spring Boot 3.2 + PostgreSQL

## Package Structure

```
com.cbt.platform.{module}/
├── entity/          # JPA entities
├── repository/      # Spring Data JPA
├── service/         # Business logic (interface + impl)
├── controller/      # REST controllers
├── dto/             # Request/Response DTOs
├── mapper/          # MapStruct mappers
└── exception/       # Module-specific exceptions
```

---

## Entity

```java
@Entity
@Table(name = "courses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String slug;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode scenarioJson;
    
    @Builder.Default
    private Boolean isActive = true;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Session> sessions = new ArrayList<>();
}
```

### Rules
- `UUID` для всех id
- Lombok: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- JSONB: `@Column(columnDefinition = "jsonb")` + `@JdbcTypeCode(SqlTypes.JSON)`
- Audit: `@CreationTimestamp`, `@UpdateTimestamp`
- Default values через `@Builder.Default`

---

## Repository

```java
@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    
    Optional<Course> findBySlug(String slug);
    
    List<Course> findAllByIsActiveTrue();
    
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.sessions WHERE c.slug = :slug")
    Optional<Course> findBySlugWithSessions(@Param("slug") String slug);
    
    boolean existsBySlug(String slug);
    
    @Modifying
    @Query("UPDATE Course c SET c.isActive = false WHERE c.id = :id")
    void softDelete(@Param("id") UUID id);
}
```

### Rules
- Extends `JpaRepository<Entity, UUID>`
- `Optional<>` для single results
- `@Query` + `JOIN FETCH` для избежания N+1
- `@Modifying` для UPDATE/DELETE queries

---

## Service

```java
// Interface
public interface CourseService {
    List<CourseDTO> findAllActive();
    CourseDetailDTO findBySlug(String slug);
    CourseDetailDTO create(CreateCourseRequest request);
    CourseDetailDTO update(UUID id, UpdateCourseRequest request);
    void delete(UUID id);
}

// Implementation
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {
    
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> findAllActive() {
        log.debug("Fetching all active courses");
        return courseRepository.findAllByIsActiveTrue()
                .stream()
                .map(courseMapper::toDto)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public CourseDetailDTO findBySlug(String slug) {
        log.debug("Fetching course: {}", slug);
        return courseRepository.findBySlug(slug)
                .map(courseMapper::toDetailDto)
                .orElseThrow(() -> new CourseNotFoundException(slug));
    }
    
    @Override
    @Transactional
    public CourseDetailDTO create(CreateCourseRequest request) {
        log.info("Creating course: {}", request.slug());
        
        if (courseRepository.existsBySlug(request.slug())) {
            throw new CourseAlreadyExistsException(request.slug());
        }
        
        Course course = courseMapper.toEntity(request);
        course = courseRepository.save(course);
        
        log.info("Course created: id={}", course.getId());
        return courseMapper.toDetailDto(course);
    }
}
```

### Rules
- Interface + Implementation
- `@RequiredArgsConstructor` для DI
- `@Slf4j` для логирования
- `@Transactional` для write, `@Transactional(readOnly = true)` для read
- Custom exceptions, не generic
- log.info для важных операций, log.debug для деталей

---

## Controller

```java
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management")
public class CourseController {
    
    private final CourseService courseService;
    
    @GetMapping
    @Operation(summary = "Get all active courses")
    public ResponseEntity<List<CourseDTO>> getAll() {
        return ResponseEntity.ok(courseService.findAllActive());
    }
    
    @GetMapping("/{slug}")
    @Operation(summary = "Get course by slug")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Found"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<CourseDetailDTO> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(courseService.findBySlug(slug));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create course (admin)")
    public ResponseEntity<CourseDetailDTO> create(
            @Valid @RequestBody CreateCourseRequest request) {
        CourseDetailDTO created = courseService.create(request);
        URI location = URI.create("/api/courses/" + created.slug());
        return ResponseEntity.created(location).body(created);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDetailDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCourseRequest request) {
        return ResponseEntity.ok(courseService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        courseService.delete(id);
    }
}
```

### Rules
- `@RestController` + `@RequestMapping`
- `ResponseEntity<>` для всех методов
- `@Valid` для request body
- Swagger: `@Tag`, `@Operation`, `@ApiResponse`
- `@PreAuthorize` для защиты
- POST возвращает 201 + Location header

---

## DTO

```java
// Request (record + validation)
public record CreateCourseRequest(
    @NotBlank(message = "Slug required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Lowercase alphanumeric with dashes")
    @Size(max = 100)
    String slug,
    
    @NotBlank(message = "Name required")
    @Size(max = 200)
    String name,
    
    @Size(max = 1000)
    String description,
    
    @NotNull(message = "Scenario required")
    JsonNode scenarioJson
) {}

// Response (record)
public record CourseDTO(
    UUID id,
    String slug,
    String name,
    String description,
    boolean isActive,
    int sessionsCount,
    LocalDateTime createdAt
) {}

public record CourseDetailDTO(
    UUID id,
    String slug,
    String name,
    String description,
    JsonNode scenarioJson,
    boolean isActive,
    BigDecimal price,
    List<SessionDTO> sessions,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

### Rules
- Records (immutable)
- Validation annotations на Request
- Отдельные DTO для list/detail views
- Нет Entity в responses

---

## Mapper (MapStruct)

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapper {
    
    CourseDTO toDto(Course course);
    
    @Mapping(target = "sessionsCount", expression = "java(course.getSessions().size())")
    CourseDTO toDtoWithCount(Course course);
    
    CourseDetailDTO toDetailDto(Course course);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "sessions", ignore = true)
    Course toEntity(CreateCourseRequest request);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Course course, UpdateCourseRequest request);
}
```

---

## Exceptions

```java
// Base
public abstract class BaseException extends RuntimeException {
    private final String code;
    private final HttpStatus status;
    
    protected BaseException(String message, String code, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }
    
    // getters
}

// Specific
public class CourseNotFoundException extends BaseException {
    public CourseNotFoundException(String slug) {
        super("Course not found: " + slug, "COURSE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}

public class CourseAlreadyExistsException extends BaseException {
    public CourseAlreadyExistsException(String slug) {
        super("Course exists: " + slug, "COURSE_EXISTS", HttpStatus.CONFLICT);
    }
}

// Handler
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBase(BaseException ex) {
        log.warn("Business error: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatus())
            .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
            .body(new ErrorResponse("VALIDATION_ERROR", message));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.internalServerError()
            .body(new ErrorResponse("INTERNAL_ERROR", "Something went wrong"));
    }
}

public record ErrorResponse(String code, String message) {}
```

---

## Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| Entity | `PascalCase` | `UserProgress` |
| Table | `snake_case` | `user_progress` |
| Repository | `{Entity}Repository` | `UserProgressRepository` |
| Service | `{Entity}Service` / `{Entity}ServiceImpl` | `CourseService` |
| Controller | `{Entity}Controller` | `CourseController` |
| DTO | `{Entity}DTO`, `Create{Entity}Request` | `CourseDTO` |
| Exception | `{Entity}{Error}Exception` | `CourseNotFoundException` |
| Mapper | `{Entity}Mapper` | `CourseMapper` |

---

## Common Patterns

### Pagination
```java
// Controller
@GetMapping
public ResponseEntity<Page<CourseDTO>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(courseService.findAll(PageRequest.of(page, size)));
}

// Service
Page<CourseDTO> findAll(Pageable pageable);
```

### Soft Delete
```java
// Entity
private LocalDateTime deletedAt;

// Repository
List<Course> findAllByDeletedAtIsNull();

// Service
void delete(UUID id) {
    Course course = findById(id);
    course.setDeletedAt(LocalDateTime.now());
    courseRepository.save(course);
}
```

### Optimistic Locking
```java
// Entity
@Version
private Long version;
```

### Caching
```java
@Service
public class CourseServiceImpl {
    
    @Cacheable(value = "courses", key = "#slug")
    public CourseDetailDTO findBySlug(String slug) { ... }
    
    @CacheEvict(value = "courses", key = "#result.slug")
    public CourseDetailDTO update(UUID id, UpdateCourseRequest request) { ... }
}
```

---

## Testing

```java
@SpringBootTest
@Transactional
class CourseServiceTest {
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Test
    void findBySlug_shouldReturnCourse() {
        // given
        Course course = Course.builder()
            .slug("test-course")
            .name("Test")
            .build();
        courseRepository.save(course);
        
        // when
        CourseDetailDTO result = courseService.findBySlug("test-course");
        
        // then
        assertThat(result.slug()).isEqualTo("test-course");
    }
    
    @Test
    void findBySlug_shouldThrowWhenNotFound() {
        assertThatThrownBy(() -> courseService.findBySlug("unknown"))
            .isInstanceOf(CourseNotFoundException.class);
    }
}
```

---

*Version 1.0*
