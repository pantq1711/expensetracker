# ==========================================
# STAGE 1: BUILD (Đóng gói code thành file .jar)
# ==========================================
# Sử dụng ảnh Java 17 chuẩn (alpine là bản Linux siêu nhẹ)
FROM eclipse-temurin:17-jdk-alpine AS build

# Đặt thư mục làm việc bên trong Docker
WORKDIR /app

# Copy các file liên quan đến cấu hình Maven vào trước
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Tải trước các thư viện (Dependencies) để cache, lần sau build sẽ cực nhanh
RUN ./mvnw dependency:go-offline

# Copy toàn bộ thư mục code (src) vào
COPY src ./src

# Chạy lệnh build ra file .jar (Bỏ qua chạy Unit Test để build cho lẹ)
RUN ./mvnw clean package -DskipTests

# ==========================================
# STAGE 2: RUN (Chạy file .jar vừa build ra)
# ==========================================
# Lúc chạy chỉ cần JRE (Môi trường chạy) cho nhẹ, không cần JDK (Môi trường code)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Lấy "chiến lợi phẩm" là file .jar từ STAGE 1 mang sang STAGE 2
COPY --from=build /app/target/*.jar app.jar

# Mở cổng 8080 cho bên ngoài gọi vào
EXPOSE 8080

# Lệnh để khởi động ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]