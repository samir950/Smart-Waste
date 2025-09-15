import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Grid,
  Chip,
  IconButton,
  Alert,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Avatar,
  LinearProgress,
  Fab
} from '@mui/material';
import {
  Add,
  Delete,
  Edit,
  LocationOn,
  Battery20,
  BatteryFull,
  Thermostat,
  Scale,
  Refresh
} from '@mui/icons-material';
import { GoogleMap, LoadScript, Marker } from '@react-google-maps/api';
import axios from 'axios';

const BinManagement = () => {
  const [bins, setBins] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedBin, setSelectedBin] = useState(null);
  const [formData, setFormData] = useState({
    binId: '',
    location: { lat: 22.5726, lng: 88.3639, address: '' },
    capacity: '',
    type: 'mixed'
  });

  const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:3000/api';

  useEffect(() => {
    fetchBins();
  }, []);

  const fetchBins = async () => {
    try {
      setLoading(true);
      // Mock data for development
      setBins([
        {
          binId: 'BIN001',
          location: { lat: 22.5726, lng: 88.3639, address: 'Park Street, Kolkata' },
          capacity: 100,
          type: 'mixed',
          fillPercentage: 75,
          weight: 80,
          status: 'needs_collection',
          batteryLevel: 85,
          temperature: 28,
          lastCollection: '2025-01-12T10:30:00Z',
          lastUpdated: new Date()
        },
        {
          binId: 'BIN002',
          location: { lat: 22.5826, lng: 88.3739, address: 'Salt Lake, Kolkata' },
          capacity: 150,
          type: 'organic',
          fillPercentage: 45,
          weight: 60,
          status: 'normal',
          batteryLevel: 92,
          temperature: 26,
          lastCollection: '2025-01-13T08:15:00Z',
          lastUpdated: new Date()
        },
        {
          binId: 'BIN003',
          location: { lat: 22.5626, lng: 88.3539, address: 'New Market, Kolkata' },
          capacity: 120,
          type: 'recyclable',
          fillPercentage: 95,
          weight: 110,
          status: 'full',
          batteryLevel: 78,
          temperature: 30,
          lastCollection: '2025-01-10T14:20:00Z',
          lastUpdated: new Date()
        }
      ]);
      setError(null);
    } catch (err) {
      setError('Failed to load bins');
      console.error('Bins error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenDialog = (bin = null) => {
    if (bin) {
      setSelectedBin(bin);
      setFormData({
        binId: bin.binId,
        location: bin.location,
        capacity: bin.capacity.toString(),
        type: bin.type
      });
    } else {
      setSelectedBin(null);
      setFormData({
        binId: '',
        location: { lat: 22.5726, lng: 88.3639, address: '' },
        capacity: '',
        type: 'mixed'
      });
    }
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setSelectedBin(null);
  };

  const handleSubmit = async () => {
    try {
      const payload = {
        ...formData,
        capacity: parseInt(formData.capacity)
      };

      if (selectedBin) {
        // Update bin (not implemented in API yet)
        console.log('Update bin:', payload);
      } else {
        // Register new bin
        await axios.post(`${API_BASE_URL}/bins/register`, payload);
      }

      fetchBins();
      handleCloseDialog();
    } catch (err) {
      setError('Failed to save bin');
      console.error('Save bin error:', err);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'full': return 'error';
      case 'needs_collection': return 'warning';
      case 'normal': return 'success';
      case 'empty': return 'info';
      default: return 'default';
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'full': return <Delete />;
      case 'needs_collection': return <Delete />;
      case 'normal': return <Delete />;
      case 'empty': return <Delete />;
      default: return <Delete />;
    }
  };

  const getBatteryIcon = (level) => {
    return level > 30 ? <BatteryFull /> : <Battery20 />;
  };

  const formatLastCollection = (dateString) => {
    if (!dateString) return 'Never';
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = Math.abs(now - date);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return `${diffDays} days ago`;
  };

  if (loading) {
    return (
      <Box sx={{ width: '100%', mt: 2 }}>
        <LinearProgress />
        <Typography sx={{ mt: 2, textAlign: 'center' }}>Loading bins...</Typography>
      </Box>
    );
  }

  return (
    <Box>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 600 }}>
          Smart Bin Management
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            variant="outlined"
            startIcon={<Refresh />}
            onClick={fetchBins}
          >
            Refresh
          </Button>
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => handleOpenDialog()}
            sx={{ borderRadius: 2 }}
          >
            Add Bin
          </Button>
        </Box>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Bin Stats */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Avatar sx={{ bgcolor: 'primary.main', mx: 'auto', mb: 2 }}>
                <Delete />
              </Avatar>
              <Typography variant="h4" sx={{ fontWeight: 600, mb: 1 }}>
                {bins.length}
              </Typography>
              <Typography color="text.secondary">
                Total Bins
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Avatar sx={{ bgcolor: 'error.main', mx: 'auto', mb: 2 }}>
                <Delete />
              </Avatar>
              <Typography variant="h4" sx={{ fontWeight: 600, mb: 1 }}>
                {bins.filter(b => b.status === 'full').length}
              </Typography>
              <Typography color="text.secondary">
                Full Bins
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Avatar sx={{ bgcolor: 'warning.main', mx: 'auto', mb: 2 }}>
                <Delete />
              </Avatar>
              <Typography variant="h4" sx={{ fontWeight: 600, mb: 1 }}>
                {bins.filter(b => b.status === 'needs_collection').length}
              </Typography>
              <Typography color="text.secondary">
                Need Collection
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Avatar sx={{ bgcolor: 'success.main', mx: 'auto', mb: 2 }}>
                <Delete />
              </Avatar>
              <Typography variant="h4" sx={{ fontWeight: 600, mb: 1 }}>
                {bins.filter(b => b.status === 'normal').length}
              </Typography>
              <Typography color="text.secondary">
                Normal
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Bins Table */}
      <Card>
        <CardContent>
          <Typography variant="h6" sx={{ mb: 2, fontWeight: 600 }}>
            Smart Bin Network
          </Typography>
          <TableContainer component={Paper} variant="outlined">
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Bin ID</TableCell>
                  <TableCell>Location</TableCell>
                  <TableCell>Type</TableCell>
                  <TableCell>Fill Level</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Battery</TableCell>
                  <TableCell>Temperature</TableCell>
                  <TableCell>Last Collection</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {bins.map((bin) => (
                  <TableRow key={bin.binId}>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <Avatar sx={{ bgcolor: 'primary.main', mr: 2, width: 32, height: 32 }}>
                          <Delete sx={{ fontSize: 16 }} />
                        </Avatar>
                        <Typography variant="body2" sx={{ fontWeight: 600 }}>
                          {bin.binId}
                        </Typography>
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <LocationOn sx={{ fontSize: 16, mr: 1, color: 'text.secondary' }} />
                        <Typography variant="body2" sx={{ maxWidth: 150 }}>
                          {bin.location.address}
                        </Typography>
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Chip 
                        label={bin.type} 
                        size="small" 
                        variant="outlined"
                        color={bin.type === 'organic' ? 'success' : bin.type === 'recyclable' ? 'info' : 'default'}
                      />
                    </TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <LinearProgress
                          variant="determinate"
                          value={bin.fillPercentage}
                          sx={{ 
                            width: 60, 
                            height: 6, 
                            borderRadius: 3,
                            backgroundColor: 'rgba(0,0,0,0.1)',
                            '& .MuiLinearProgress-bar': {
                              backgroundColor: bin.fillPercentage > 80 ? '#F44336' : 
                                             bin.fillPercentage > 60 ? '#FF9800' : '#4CAF50',
                            }
                          }}
                        />
                        <Typography variant="caption">
                          {bin.fillPercentage}%
                        </Typography>
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Chip
                        icon={getStatusIcon(bin.status)}
                        label={bin.status.replace('_', ' ')}
                        color={getStatusColor(bin.status)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        {getBatteryIcon(bin.batteryLevel)}
                        <Typography variant="caption">
                          {bin.batteryLevel}%
                        </Typography>
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Thermostat sx={{ fontSize: 16, color: 'text.secondary' }} />
                        <Typography variant="caption">
                          {bin.temperature}°C
                        </Typography>
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Typography variant="caption">
                        {formatLastCollection(bin.lastCollection)}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <IconButton 
                        size="small" 
                        onClick={() => handleOpenDialog(bin)}
                        color="primary"
                      >
                        <Edit />
                      </IconButton>
                      <IconButton size="small" color="error">
                        <Delete />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>

      {/* Add/Edit Bin Dialog */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="md" fullWidth>
        <DialogTitle>
          {selectedBin ? 'Edit Smart Bin' : 'Add New Smart Bin'}
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Bin ID"
                value={formData.binId}
                onChange={(e) => setFormData({ ...formData, binId: e.target.value })}
                disabled={!!selectedBin}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Bin Type</InputLabel>
                <Select
                  value={formData.type}
                  label="Bin Type"
                  onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                >
                  <MenuItem value="mixed">Mixed Waste</MenuItem>
                  <MenuItem value="organic">Organic</MenuItem>
                  <MenuItem value="recyclable">Recyclable</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Capacity (kg)"
                type="number"
                value={formData.capacity}
                onChange={(e) => setFormData({ ...formData, capacity: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Address"
                value={formData.location.address}
                onChange={(e) => setFormData({ 
                  ...formData, 
                  location: { ...formData.location, address: e.target.value }
                })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Latitude"
                type="number"
                value={formData.location.lat}
                onChange={(e) => setFormData({ 
                  ...formData, 
                  location: { ...formData.location, lat: parseFloat(e.target.value) }
                })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Longitude"
                type="number"
                value={formData.location.lng}
                onChange={(e) => setFormData({ 
                  ...formData, 
                  location: { ...formData.location, lng: parseFloat(e.target.value) }
                })}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancel</Button>
          <Button onClick={handleSubmit} variant="contained">
            {selectedBin ? 'Update' : 'Add'} Bin
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default BinManagement;