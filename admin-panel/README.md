# Smart Waste Management - Admin Panel

A comprehensive React-based admin panel for managing smart waste collection operations, built with Material-UI and real-time capabilities.

## 🌟 Features

### Dashboard
- **Real-time Overview**: Live statistics and metrics
- **Collection Trends**: Interactive charts showing daily collection patterns
- **Bin Status Distribution**: Visual representation of bin fill levels
- **Active Alerts**: Immediate notifications for urgent collections
- **Recent Activity**: Timeline of system events

### Vehicle Management
- **Fleet Overview**: Complete vehicle inventory with status tracking
- **Real-time Location**: GPS tracking of all vehicles
- **Fuel Monitoring**: Fuel level tracking and alerts
- **Driver Management**: Driver assignment and performance tracking
- **Maintenance Scheduling**: Preventive maintenance alerts

### Bin Management
- **Smart Bin Network**: Complete bin inventory with IoT sensor data
- **Fill Level Monitoring**: Real-time fill percentage tracking
- **Collection Priority**: Automated priority queue for collections
- **Maintenance Tracking**: Issue reporting and resolution
- **Geographic Distribution**: Map-based bin location management

### Route Management
- **Route Optimization**: AI-powered route planning
- **Progress Tracking**: Real-time route completion monitoring
- **Performance Analytics**: Route efficiency metrics
- **Dynamic Routing**: Adaptive routing based on real-time conditions

### Analytics & Reporting
- **Environmental Impact**: Carbon footprint and fuel savings
- **Performance Metrics**: KPIs and efficiency measurements
- **Predictive Analytics**: Forecasting and trend analysis
- **Custom Reports**: Exportable reports in multiple formats

## 🚀 Technology Stack

- **Frontend**: React 18 with Material-UI 5
- **State Management**: React Context API
- **Charts**: Recharts for data visualization
- **Maps**: Google Maps API integration
- **Real-time**: Socket.io client
- **HTTP Client**: Axios
- **Routing**: React Router v6
- **Styling**: Material-UI theming system

## 📦 Installation

```bash
# Clone the repository
git clone <repository-url>
cd admin-panel

# Install dependencies
npm install

# Set up environment variables
cp .env.example .env
# Edit .env with your configuration

# Start development server
npm start
```

## 🔧 Configuration

Create a `.env` file in the root directory:

```env
REACT_APP_API_URL=http://localhost:3000/api
REACT_APP_SOCKET_URL=http://localhost:3000
REACT_APP_GOOGLE_MAPS_API_KEY=your_google_maps_api_key
```

## 🏗️ Project Structure

```
src/
├── components/
│   ├── Auth/           # Authentication components
│   ├── Dashboard/      # Dashboard overview
│   ├── Vehicles/       # Vehicle management
│   ├── Bins/          # Bin management
│   ├── Routes/        # Route management
│   ├── Analytics/     # Analytics and reporting
│   └── Layout/        # Layout components
├── contexts/          # React contexts
├── utils/            # Utility functions
├── hooks/            # Custom React hooks
└── services/         # API services
```

## 🎨 Design System

### Color Palette
- **Primary Green**: #4CAF50 (Eco-friendly theme)
- **Secondary Blue**: #2196F3 (Information)
- **Warning Orange**: #FF9800 (Alerts)
- **Error Red**: #F44336 (Critical issues)
- **Success Green**: #4CAF50 (Completed actions)

### Typography
- **Font Family**: Roboto
- **Headings**: 600 weight for emphasis
- **Body Text**: 400 weight for readability
- **Captions**: 300 weight for secondary information

### Components
- **Cards**: Elevated design with hover effects
- **Buttons**: Rounded corners with consistent styling
- **Tables**: Clean, sortable data presentation
- **Charts**: Consistent color scheme and responsive design

## 📱 Responsive Design

The admin panel is fully responsive and optimized for:
- **Desktop**: Full-featured dashboard experience
- **Tablet**: Adapted layout with touch-friendly controls
- **Mobile**: Simplified interface with drawer navigation

## 🔐 Authentication

- **JWT-based**: Secure token authentication
- **Role-based Access**: Admin-only access control
- **Session Management**: Automatic token refresh
- **Secure Storage**: Local storage with encryption

## 📊 Real-time Features

- **Live Updates**: Socket.io integration for real-time data
- **Vehicle Tracking**: GPS location updates
- **Bin Status**: Sensor data streaming
- **Route Progress**: Live route completion updates
- **Alerts**: Instant notifications for critical events

## 🧪 Testing

```bash
# Run tests
npm test

# Run tests with coverage
npm test -- --coverage

# Run tests in watch mode
npm test -- --watch
```

## 🚀 Deployment

### Development
```bash
npm start
```

### Production Build
```bash
npm run build
```

### Docker Deployment
```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY build ./build
RUN npm install -g serve
EXPOSE 3000
CMD ["serve", "-s", "build", "-l", "3000"]
```

## 🔧 API Integration

The admin panel integrates with the Smart Waste Management API:

### Endpoints Used
- `GET /api/analytics/dashboard` - Dashboard statistics
- `GET /api/vehicles/overview` - Vehicle fleet data
- `GET /api/bins/collection-priority` - Urgent bin collections
- `GET /api/routes/active` - Active route monitoring
- `POST /api/vehicles/register` - Vehicle registration
- `POST /api/routes/optimize` - Route optimization

### Real-time Events
- `vehicle-update` - Vehicle location/status changes
- `bin-update` - Bin sensor data updates
- `route-progress` - Route completion updates
- `bin-collected` - Collection completion events

## 🎯 Performance Optimization

- **Code Splitting**: Lazy loading of route components
- **Memoization**: React.memo for expensive components
- **Virtual Scrolling**: Efficient large data set rendering
- **Image Optimization**: Compressed assets and lazy loading
- **Bundle Analysis**: Webpack bundle analyzer integration

## 🔍 Monitoring & Analytics

- **Error Tracking**: Comprehensive error logging
- **Performance Metrics**: Core Web Vitals monitoring
- **User Analytics**: Usage pattern tracking
- **API Monitoring**: Request/response time tracking

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Development Team

**Developed by QBrain Team**
- Modern React architecture
- Material-UI design system
- Real-time data integration
- Responsive mobile-first design
- Production-ready deployment

## 🆘 Support

For technical support or feature requests:
- Create an issue in the repository
- Contact the development team
- Check the API documentation

---

**Built with ❤️ for sustainable waste management**