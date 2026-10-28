#!/bin/bash
set -e

echo "=========================================================="
echo "    KHOI DONG HE THONG TREN GOOGLE CLOUD SHELL (FREE)     "
echo "=========================================================="

# 1. Khoi dong MySQL tren Cloud Shell bang Docker
echo "[1/5] Khoi tao co so du lieu MySQL tren Cloud Shell..."
if ! docker ps -a --format '{{.Names}}' | grep -q "^mysql-cms$"; then
    docker run -d --name mysql-cms -p 3306:3306 \
      -e MYSQL_ROOT_PASSWORD=123456789 \
      -e MYSQL_DATABASE=course_db \
      mysql:8.0
else
    docker start mysql-cms || true
fi

echo "Dang doi MySQL san sang (15 giay)..."
sleep 15

# 2. Tai Cloudflared cho Linux neu chua co
echo "[2/5] Cai dat cong cu tao URL cong khai Cloudflared..."
if [ ! -f "./cloudflared" ]; then
    wget -q https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-amd64 -O cloudflared
    chmod +x cloudflared
fi

# 3. Build file JAR Spring Boot
echo "[3/5] Dang bien dich va dong goi ung dung Spring Boot..."
chmod +x mvnw
./mvnw clean package -DskipTests

# 4. Chay Spring Boot ngam
echo "[4/5] Dang khoi dong Spring Boot tren cong 8080..."
pkill -f "course-management-system" || true
nohup java -jar target/course-management-system-0.0.1-SNAPSHOT.jar > spring.log 2>&1 &

echo "Dang cho ung dung khoi dong xong..."
for i in {1..30}; do
    if curl -s http://localhost:8080 > /dev/null; then
        echo "Spring Boot da chay thanh cong!"
        break
    fi
    sleep 2
done

# 5. Khoi tao URL cong khai Cloudflare Tunnel
echo "[5/5] Dang tao URL cong khai qua Internet cho giao vien..."
echo "=========================================================="
echo "  URL CONG KHAI CHO GIAO VIEN SE HIEN O BEN DUOI (https://...trycloudflare.com)"
echo "  Nhan Ctrl+C neu muon dung lai."
echo "=========================================================="

./cloudflared tunnel --url http://localhost:8080
