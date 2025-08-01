# EVCentral 3.7.1.2 Docker Deployment

This guide explains how to deploy EVCentral on CentOS using Docker.

## 🐳 Quick Start

1. **On your CentOS server**, run the deployment script:
```bash
curl -fsSL https://raw.githubusercontent.com/msukhum/evcentral3712/main/deploy-centos.sh | bash
```

Or manually:
```bash
git clone https://github.com/msukhum/evcentral3712.git
cd evcentral3712
chmod +x deploy-centos.sh
./deploy-centos.sh
```

## 📋 Prerequisites

- CentOS 7/8/9 or RHEL 8/9
- Docker and Docker Compose installed
- MySQL server accessible at 185.78.165.84:3306
- Ports 8180 and 8443 open in firewall

## 🔧 Configuration

### Database Connection
- **Host**: 185.78.165.84:3306
- **Database**: steve37db
- **User**: steve
- **Password**: thrnnvtn

### Application Settings
- **Web Interface**: http://your-server:8180/manager/dashboard
- **Login**: admin / 1234567
- **WebSocket Endpoint**: ws://your-server:8180/ocpp/(chargeBoxId)

### HikariCP Connection Pool
- **Max Pool Size**: 30 connections
- **Min Idle**: 5 connections
- **Max Lifetime**: 30 minutes
- **Connection Timeout**: 30 seconds
- **Idle Timeout**: 10 minutes

## 📁 Directory Structure

```
/opt/evcentral3712/
├── docker-compose.yml
├── Dockerfile
├── logs/                    # Application logs (persisted)
│   ├── steve.log
│   └── steve-YYYY-MM-DD-*.log.gz
├── src/
└── target/
```

## 🔍 Monitoring

### View Logs
```bash
cd /opt/evcentral3712
docker-compose logs -f
```

### Check Status
```bash
docker-compose ps
docker-compose top
```

### WebSocket Monitoring
The application includes enhanced WebSocket connection monitoring:
- Connection establishment/disconnection tracking
- Duration monitoring for charge points
- Automatic reconnection handling
- Statistics logging every 5 minutes

## 🛠️ Management Commands

```bash
# Start application
docker-compose up -d

# Stop application
docker-compose down

# Restart application
docker-compose restart

# Rebuild and start
docker-compose up --build -d

# View real-time logs
docker-compose logs -f

# Check resource usage
docker stats $(docker-compose ps -q)
```

## 🔧 Troubleshooting

### Common Issues

1. **Port already in use**
   ```bash
   sudo netstat -tlnp | grep :8180
   sudo kill -9 <pid>
   ```

2. **Database connection failed**
   - Check if MySQL is accessible: `telnet 185.78.165.84 3306`
   - Verify credentials in docker/main.properties
   - Check firewall rules

3. **Container won't start**
   ```bash
   docker-compose logs
   docker system df
   docker system prune
   ```

4. **Out of disk space**
   ```bash
   # Clean old logs
   find /opt/evcentral3712/logs -name "*.gz" -mtime +30 -delete
   
   # Clean Docker
   docker system prune -af
   ```

## 🔒 Security Notes

- Change default passwords in production
- Use HTTPS in production (configure keystore)
- Restrict firewall rules to necessary IPs
- Regular security updates

## 📊 Performance Tuning

### For High Load
Adjust in `src/main/resources/config/docker/main.properties`:
```properties
# Increase connection pool
db.hikari.maximum_pool_size = 50
db.hikari.minimum_idle = 10

# Enable gzip compression
server.gzip.enabled = true
```

### For Low Memory
```properties
# Reduce connection pool
db.hikari.maximum_pool_size = 10
db.hikari.minimum_idle = 2
```

## 🆕 Updates

To update to a new version:
```bash
cd /opt/evcentral3712
git pull origin main
docker-compose down
docker-compose up --build -d
```

## 🐛 Support

- GitHub Issues: https://github.com/msukhum/evcentral3712/issues
- Original SteVe: https://github.com/steve-community/steve
- OCPP Specification: https://www.openchargealliance.org/

## 📄 License

This project is based on SteVe and follows the GPL-3.0 license.
