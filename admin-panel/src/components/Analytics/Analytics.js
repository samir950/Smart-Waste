import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  Avatar,
  LinearProgress,
  Alert,
  Button,
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
  Chip
} from '@mui/material';
import {
  TrendingUp,
  Eco,
  LocalGasStation,
  Speed,
  Assessment,
  Download,
  DateRange
} from '@mui/icons-material';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  AreaChart,
  Area
} from 'recharts';
import axios from 'axios';

const Analytics = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [period, setPeriod] = useState('monthly');
  const [analyticsData, setAnalyticsData] = useState(null);
  const [environmentalData, setEnvironmentalData] = useState(null);
  const [predictions, setPredictions] = useState(null);

  const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:3000/api';

  useEffect(() => {
    fetchAnalytics();
  }, [period]);

  const fetchAnalytics = async () => {
    try {
      setLoading(true);
      
      // Mock data for development
      setAnalyticsData({
        collectionTrends: [
          { date: '2025-01-01', collections: 45, efficiency: 85, wasteCollected: 2250 },
          { date: '2025-01-02', collections: 52, efficiency: 88, wasteCollected: 2600 },
          { date: '2025-01-03', collections: 48, efficiency: 92, wasteCollected: 2400 },
          { date: '2025-01-04', collections: 55, efficiency: 90, wasteCollected: 2750 },
          { date: '2025-01-05', collections: 49, efficiency: 87, wasteCollected: 2450 },
          { date: '2025-01-06', collections: 58, efficiency: 94, wasteCollected: 2900 },
          { date: '2025-01-07', collections: 53, efficiency: 91, wasteCollected: 2650 }
        ],
        vehiclePerformance: [
          { vehicle: 'VH001', collections: 156, efficiency: 92, fuelUsed: 245 },
          { vehicle: 'VH002', collections: 142, efficiency: 88, fuelUsed: 268 },
          { vehicle: 'VH003', collections: 134, efficiency: 85, fuelUsed: 289 },
          { vehicle: 'VH004', collections: 128, efficiency: 90, fuelUsed: 234 }
        ],
        wasteTypes: [
          { name: 'Mixed', value: 45, color: '#8884d8' },
          { name: 'Organic', value: 30, color: '#82ca9d' },
          { name: 'Recyclable', value: 25, color: '#ffc658' }
        ]
      });

      setEnvironmentalData({
        fuelSaved: 1250,
        carbonReduced: 3340,
        efficiencyImprovement: 28,
        costSavings: 100000,
        wasteProcessed: 45600
      });

      setPredictions({
        binFillPredictions: [
          {
            binId: 'BIN001',
            predictedFillTime: '2025-01-15T14:30:00Z',
            confidence: 85,
            currentFillLevel: 75,
            estimatedHoursToFill: 18
          },
          {
            binId: 'BIN003',
            predictedFillTime: '2025-01-15T16:45:00Z',
            confidence: 92,
            currentFillLevel: 82,
            estimatedHoursToFill: 12
          },
          {
            binId: 'BIN007',
            predictedFillTime: '2025-01-16T09:15:00Z',
            confidence: 78,
            currentFillLevel: 68,
            estimatedHoursToFill: 28
          }
        ],
        maintenanceAlerts: [
          {
            vehicleId: 'VH002',
            nextMaintenance: '2025-01-20',
            riskLevel: 'medium',
            kmToMaintenance: 1200,
            estimatedDays: 24
          },
          {
            vehicleId: 'VH004',
            nextMaintenance: '2025-01-18',
            riskLevel: 'high',
            kmToMaintenance: 450,
            estimatedDays: 9
          }
        ]
      });

      setError(null);
    } catch (err) {
      setError('Failed to load analytics data');
      console.error('Analytics error:', err);
    } finally {
      setLoading(false);
    }
  };

  const generateReport = async () => {
    try {
      const reportData = {
        reportType: 'waste_collection',
        dateRange: {
          startDate: '2025-01-01',
          endDate: '2025-01-14'
        },
        filters: {},
        format: 'pdf'
      };

      // In production, make API call
      console.log('Generating report:', reportData);
      alert('Report generation started. You will receive an email when ready.');
    } catch (err) {
      console.error('Report generation error:', err);
    }
  };

  const getRiskColor = (level) => {
    switch (level) {
      case 'high': return 'error';
      case 'medium': return 'warning';
      case 'low': return 'success';
      default: return 'default';
    }
  };

  if (loading) {
    return (
      <Box sx={{ width: '100%', mt: 2 }}>
        <LinearProgress />
        <Typography sx={{ mt: 2, textAlign: 'center' }}>Loading analytics...</Typography>
      </Box>
    );
  }

  return (
    <Box>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 600 }}>
          Analytics & Insights
        </Typography>
        <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel>Period</InputLabel>
            <Select
              value={period}
              label="Period"
              onChange={(e) => setPeriod(e.target.value)}
            >
              <MenuItem value="daily">Daily</MenuItem>
              <MenuItem value="weekly">Weekly</MenuItem>
              <MenuItem value="monthly">Monthly</MenuItem>
              <MenuItem value="yearly">Yearly</MenuItem>
            </Select>
          </FormControl>
          <Button
            variant="contained"
            startIcon={<Download />}
            onClick={generateReport}
          >
            Generate Report
          </Button>
        </Box>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Environmental Impact Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={2.4}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Avatar sx={{ bgcolor: 'success.main', mx: 'auto', mb: 2 }}>
                <LocalGasStation />
              </Avatar>
              <Typography variant="h5" sx={{ fontWeight: 600, mb: 1 }}>
                {environmentalData?.fuelSaved || 0}L
              </Typography>
              <Typography color="text.secondary" variant="body2">
                Fuel Saved
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Avatar sx={{ bgcolor: 'info.main', mx: 'auto', mb: 2 }}>
                <Eco />
              </Avatar>
              <Typography variant="h5" sx={{ fontWeight: 600, mb: 1 }}>
                {environmentalData?.carbonReduced || 0}kg
              </Typography>
              <Typography color="text.secondary" variant="body2">
                CO₂ Reduced
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Avatar sx={{ bgcolor: 'warning.main', mx: 'auto', mb: 2 }}>
                <Speed />
              </Avatar>
              <Typography variant="h5" sx={{ fontWeight: 600, mb: 1 }}>
                {environmentalData?.efficiencyImprovement || 0}%
              </Typography>
              <Typography color="text.secondary" variant="body2">
                Efficiency Gain
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Avatar sx={{ bgcolor: 'primary.main', mx: 'auto', mb: 2 }}>
                <TrendingUp />
              </Avatar>
              <Typography variant="h5" sx={{ fontWeight: 600, mb: 1 }}>
                ₹{(environmentalData?.costSavings || 0).toLocaleString()}
              </Typography>
              <Typography color="text.secondary" variant="body2">
                Cost Savings
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Avatar sx={{ bgcolor: 'secondary.main', mx: 'auto', mb: 2 }}>
                <Assessment />
              </Avatar>
              <Typography variant="h5" sx={{ fontWeight: 600, mb: 1 }}>
                {(environmentalData?.wasteProcessed || 0).toLocaleString()}kg
              </Typography>
              <Typography color="text.secondary" variant="body2">
                Waste Processed
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        {/* Collection Trends */}
        <Grid item xs={12} lg={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 3, fontWeight: 600 }}>
                Collection Trends & Efficiency
              </Typography>
              <ResponsiveContainer width="100%" height={350}>
                <AreaChart data={analyticsData?.collectionTrends || []}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" />
                  <YAxis yAxisId="left" />
                  <YAxis yAxisId="right" orientation="right" />
                  <Tooltip />
                  <Area
                    yAxisId="left"
                    type="monotone"
                    dataKey="collections"
                    stackId="1"
                    stroke="#4CAF50"
                    fill="#4CAF50"
                    fillOpacity={0.6}
                  />
                  <Line
                    yAxisId="right"
                    type="monotone"
                    dataKey="efficiency"
                    stroke="#2196F3"
                    strokeWidth={3}
                    dot={{ fill: '#2196F3', strokeWidth: 2, r: 4 }}
                  />
                </AreaChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>

        {/* Waste Type Distribution */}
        <Grid item xs={12} lg={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 3, fontWeight: 600 }}>
                Waste Type Distribution
              </Typography>
              <ResponsiveContainer width="100%" height={350}>
                <PieChart>
                  <Pie
                    data={analyticsData?.wasteTypes || []}
                    cx="50%"
                    cy="50%"
                    innerRadius={60}
                    outerRadius={120}
                    paddingAngle={5}
                    dataKey="value"
                  >
                    {(analyticsData?.wasteTypes || []).map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
              <Box sx={{ mt: 2 }}>
                {(analyticsData?.wasteTypes || []).map((item, index) => (
                  <Box key={index} sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <Box 
                      sx={{ 
                        width: 12, 
                        height: 12, 
                        backgroundColor: item.color, 
                        borderRadius: '50%', 
                        mr: 1 
                      }} 
                    />
                    <Typography variant="body2" sx={{ flexGrow: 1 }}>
                      {item.name}
                    </Typography>
                    <Typography variant="body2" sx={{ fontWeight: 600 }}>
                      {item.value}%
                    </Typography>
                  </Box>
                ))}
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Vehicle Performance */}
        <Grid item xs={12} lg={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 3, fontWeight: 600 }}>
                Vehicle Performance Comparison
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={analyticsData?.vehiclePerformance || []}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="vehicle" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="collections" fill="#4CAF50" />
                  <Bar dataKey="efficiency" fill="#2196F3" />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>

        {/* Predictive Analytics */}
        <Grid item xs={12} lg={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 3, fontWeight: 600 }}>
                Predictive Insights
              </Typography>
              
              <Typography variant="subtitle2" sx={{ mb: 2, fontWeight: 600 }}>
                Bin Fill Predictions
              </Typography>
              <TableContainer component={Paper} variant="outlined" sx={{ mb: 3 }}>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Bin ID</TableCell>
                      <TableCell>Current Fill</TableCell>
                      <TableCell>Predicted Full</TableCell>
                      <TableCell>Confidence</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {(predictions?.binFillPredictions || []).map((prediction) => (
                      <TableRow key={prediction.binId}>
                        <TableCell>{prediction.binId}</TableCell>
                        <TableCell>{prediction.currentFillLevel}%</TableCell>
                        <TableCell>
                          {new Date(prediction.predictedFillTime).toLocaleDateString()}
                        </TableCell>
                        <TableCell>
                          <Chip 
                            label={`${prediction.confidence}%`} 
                            size="small"
                            color={prediction.confidence > 80 ? 'success' : 'warning'}
                          />
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>

              <Typography variant="subtitle2" sx={{ mb: 2, fontWeight: 600 }}>
                Maintenance Alerts
              </Typography>
              <TableContainer component={Paper} variant="outlined">
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Vehicle</TableCell>
                      <TableCell>Next Service</TableCell>
                      <TableCell>Risk Level</TableCell>
                      <TableCell>Days Left</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {(predictions?.maintenanceAlerts || []).map((alert) => (
                      <TableRow key={alert.vehicleId}>
                        <TableCell>{alert.vehicleId}</TableCell>
                        <TableCell>{alert.nextMaintenance}</TableCell>
                        <TableCell>
                          <Chip 
                            label={alert.riskLevel} 
                            size="small"
                            color={getRiskColor(alert.riskLevel)}
                          />
                        </TableCell>
                        <TableCell>{alert.estimatedDays}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Analytics;