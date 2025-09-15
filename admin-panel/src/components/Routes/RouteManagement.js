import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Grid,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Avatar,
  LinearProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  List,
  ListItem,
  ListItemText,
  ListItemIcon
} from '@mui/material';
import {
  Route,
  Add,
  LocalShipping,
  Timeline,
  Speed,
  LocationOn,
  CheckCircle,
  Schedule,
  Refresh
} from '@mui/icons-material';
import { GoogleMap, LoadScript, Marker, DirectionsRenderer } from '@react-google-maps/api';
import axios from 'axios';

const RouteManagement = () => {
  const [routes, setRoutes] = useState([]);
  const [vehicles, setVehicles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [formData, setFormData] = useState({
    vehicleId: '',
    startLocation: { lat: 22.5726, lng: 88.3639 },
    maxCapacity: '',
    timeConstraints: {
      startTime: '06:00',
      maxDuration: 480
    }
  });

  const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:3000/api';

  useEffect(() => {
    fetchRoutes();
    fetchVehicles();
  }, []);

  const fetchRoutes = async () => {
    try {
      setLoading(true);
      // Mock data for development
      setRoutes([
        {
          routeId: 'RT001',
          vehicleId: 'VH001',
          status: 'active',
          progress: 65,
          totalBins: 8,
          completedBins: 5,
          estimatedTime: 120,
          remainingTime: 42,
          estimatedDistance: 25.4,
          currentBin: 'BIN006',
          startTime: '2025-01-14T06:00:00Z',
          estimatedCompletion: '2025-01-14T08:00:00Z'
        },
        {
          routeId: 'RT002',
          vehicleId: 'VH002',
          status: 'planned',
          progress: 0,
          totalBins: 12,
          completedBins: 0,
          estimatedTime: 180,
          remainingTime: 180,
          estimatedDistance: 32.1,
          currentBin: null,
          startTime: '2025-01-14T08:00:00Z',
          estimatedCompletion: '2025-01-14T11:00:00Z'
        },
        {
          routeId: 'RT003',
          vehicleId: 'VH003',
          status: 'completed',
          progress: 100,
          totalBins: 6,
          completedBins: 6,
          estimatedTime: 90,
          remainingTime: 0,
          estimatedDistance: 18.7,
          currentBin: null,
          startTime: '2025-01-14T05:30:00Z',
          estimatedCompletion: '2025-01-14T07:00:00Z'
        }
      ]);
      setError(null);
    } catch (err) {
      setError('Failed to load routes');
      console.error('Routes error:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchVehicles = async () => {
    try {
      // Mock vehicles data
      setVehicles([
        { vehicleId: 'VH001', driverName: 'John Doe', status: 'collecting' },
        { vehicleId: 'VH002', driverName: 'Jane Smith', status: 'idle' },
        { vehicleId: 'VH003', driverName: 'Mike Johnson', status: 'idle' }
      ]);
    } catch (err) {
      console.error('Vehicles error:', err);
    }
  };

  const handleOptimizeRoute = async () => {
    try {
      const payload = {
        vehicleId: formData.vehicleId,
        startLocation: formData.startLocation,
        maxCapacity: parseInt(formData.maxCapacity),
        timeConstraints: formData.timeConstraints
      };

      // In production, make API call
      // await axios.post(`${API_BASE_URL}/routes/optimize`, payload);
      
      console.log('Optimizing route:', payload);
      fetchRoutes();
      setOpenDialog(false);
    } catch (err) {
      setError('Failed to optimize route');
      console.error('Route optimization error:', err);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'active': return 'success';
      case 'planned': return 'info';
      case 'completed': return 'default';
      case 'paused': return 'warning';
      default: return 'default';
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'active': return <Speed />;
      case 'planned': return <Schedule />;
      case 'completed': return <CheckCircle />;
      default: return <Route />;
    }
  };

  const formatTime = (minutes) => {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours}h ${mins}m`;
  };

  const formatDateTime = (dateString) => {
    return new Date(dateString).toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (loading) {
    return (
      <Box sx={{ width: '100%', mt: 2 }}>
        <LinearProgress />
        <Typography sx={{ mt: 2, textAlign: 'center' }}>Loading routes...</Typography>
      </Box>
    );
  }

  return (
    <Box>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 600 }}>
          Route Management
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            variant="outlined"
            startIcon={<Refresh />}
            onClick={fetchRoutes}
          >
            Refresh
          </Button>
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => setOpenDialog(true)}
            sx={{ borderRadius: 2 }}
          >
            Optimize Route
          </Button>
        </Box>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Route Stats */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Avatar sx={{ bgcolor: 'primary.main', mx: 'auto', mb: 2 }}>
                <Route />
              </Avatar>
              <Typography variant="h4" sx={{ fontWeight: 600, mb: 1 }}>
                {routes.length}
              </Typography>
              <Typography color="text.secondary">
                Total Routes
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Avatar sx={{ bgcolor: 'success.main', mx: 'auto', mb: 2 }}>
                <Speed />
              </Avatar>
              <Typography variant="h4" sx={{ fontWeight: 600, mb: 1 }}>
                {routes.filter(r => r.status === 'active').length}
              </Typography>
              <Typography color="text.secondary">
                Active Routes
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Avatar sx={{ bgcolor: 'info.main', mx: 'auto', mb: 2 }}>
                <Schedule />
              </Avatar>
              <Typography variant="h4" sx={{ fontWeight: 600, mb: 1 }}>
                {routes.filter(r => r.status === 'planned').length}
              </Typography>
              <Typography color="text.secondary">
                Planned Routes
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Avatar sx={{ bgcolor: 'warning.main', mx: 'auto', mb: 2 }}>
                <CheckCircle />
              </Avatar>
              <Typography variant="h4" sx={{ fontWeight: 600, mb: 1 }}>
                {routes.filter(r => r.status === 'completed').length}
              </Typography>
              <Typography color="text.secondary">
                Completed Today
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Routes Table */}
      <Card>
        <CardContent>
          <Typography variant="h6" sx={{ mb: 2, fontWeight: 600 }}>
            Route Operations
          </Typography>
          <TableContainer component={Paper} variant="outlined">
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Route ID</TableCell>
                  <TableCell>Vehicle</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Progress</TableCell>
                  <TableCell>Bins</TableCell>
                  <TableCell>Time</TableCell>
                  <TableCell>Distance</TableCell>
                  <TableCell>Current Location</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {routes.map((route) => (
                  <TableRow key={route.routeId}>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <Avatar sx={{ bgcolor: 'primary.main', mr: 2, width: 32, height: 32 }}>
                          <Route sx={{ fontSize: 16 }} />
                        </Avatar>
                        <Typography variant="body2" sx={{ fontWeight: 600 }}>
                          {route.routeId}
                        </Typography>
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <LocalShipping sx={{ fontSize: 16, mr: 1, color: 'text.secondary' }} />
                        <Typography variant="body2">
                          {route.vehicleId}
                        </Typography>
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Chip
                        icon={getStatusIcon(route.status)}
                        label={route.status}
                        color={getStatusColor(route.status)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <LinearProgress
                          variant="determinate"
                          value={route.progress}
                          sx={{ 
                            width: 80, 
                            height: 6, 
                            borderRadius: 3,
                            backgroundColor: 'rgba(0,0,0,0.1)',
                            '& .MuiLinearProgress-bar': {
                              backgroundColor: route.progress === 100 ? '#4CAF50' : '#2196F3',
                            }
                          }}
                        />
                        <Typography variant="caption">
                          {route.progress}%
                        </Typography>
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2">
                        {route.completedBins}/{route.totalBins}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Box>
                        <Typography variant="body2">
                          {formatTime(route.estimatedTime)}
                        </Typography>
                        {route.remainingTime > 0 && (
                          <Typography variant="caption" color="text.secondary">
                            {formatTime(route.remainingTime)} left
                          </Typography>
                        )}
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2">
                        {route.estimatedDistance} km
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <LocationOn sx={{ fontSize: 16, mr: 1, color: 'text.secondary' }} />
                        <Typography variant="body2">
                          {route.currentBin || 'Depot'}
                        </Typography>
                      </Box>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>

      {/* Route Optimization Dialog */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>
          Optimize New Route
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Select Vehicle</InputLabel>
                <Select
                  value={formData.vehicleId}
                  label="Select Vehicle"
                  onChange={(e) => setFormData({ ...formData, vehicleId: e.target.value })}
                >
                  {vehicles.filter(v => v.status === 'idle').map((vehicle) => (
                    <MenuItem key={vehicle.vehicleId} value={vehicle.vehicleId}>
                      {vehicle.vehicleId} - {vehicle.driverName}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Max Capacity (kg)"
                type="number"
                value={formData.maxCapacity}
                onChange={(e) => setFormData({ ...formData, maxCapacity: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Start Time"
                type="time"
                value={formData.timeConstraints.startTime}
                onChange={(e) => setFormData({ 
                  ...formData, 
                  timeConstraints: { ...formData.timeConstraints, startTime: e.target.value }
                })}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Max Duration (minutes)"
                type="number"
                value={formData.timeConstraints.maxDuration}
                onChange={(e) => setFormData({ 
                  ...formData, 
                  timeConstraints: { ...formData.timeConstraints, maxDuration: parseInt(e.target.value) }
                })}
              />
            </Grid>
            <Grid item xs={12}>
              <Typography variant="subtitle2" sx={{ mb: 1 }}>
                Route Optimization Criteria:
              </Typography>
              <List dense>
                <ListItem>
                  <ListItemIcon>
                    <CheckCircle color="success" />
                  </ListItemIcon>
                  <ListItemText primary="Prioritize bins with highest fill levels" />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <CheckCircle color="success" />
                  </ListItemIcon>
                  <ListItemText primary="Minimize total travel distance" />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <CheckCircle color="success" />
                  </ListItemIcon>
                  <ListItemText primary="Consider traffic patterns and time constraints" />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <CheckCircle color="success" />
                  </ListItemIcon>
                  <ListItemText primary="Optimize fuel efficiency" />
                </ListItem>
              </List>
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button onClick={handleOptimizeRoute} variant="contained">
            Optimize Route
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default RouteManagement;