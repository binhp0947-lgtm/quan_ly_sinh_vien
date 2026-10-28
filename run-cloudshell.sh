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
pkill -f "cloudflared" || true
rm -f tunnel.log

nohup ./cloudflared tunnel --url http://localhost:8080 > tunnel.log 2>&1 &

echo "Dang lay link cong khai Cloudflare (khoang 5 giay)..."
URL=""
for i in {1..30}; do
    URL=$(grep -o 'https://[-a-zA-Z0-9@:%._\+~#=]\+\.trycloudflare\.com' tunnel.log | head -n 1 || true)
    if [ -n "$URL" ]; then
        echo ""
        echo "=========================================================="
        echo "   🎉 HE THONG DA HOAT DONG VA CO LINK CONG KHAI!       "
        echo "=========================================================="
        echo ""
        echo "   👉 LINK GUI GIAO VIEN: $URL"
        echo "   👉 LINK SWAGGER DOCS:  $URL/swagger-ui/index.html"
        echo ""
        echo "=========================================================="
        echo "   (He thong & MySQL dang chay tren Google Cloud Shell)"
        echo "=========================================================="
        break
    fi
    sleep 1
done

if [ -z "$URL" ]; then
    cat tunnel.log
fi

# Giu terminal theo doi tunnel
tail -f tunnel.log

