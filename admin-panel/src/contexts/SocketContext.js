import React, { createContext, useContext, useEffect, useState } from 'react';
import io from 'socket.io-client';

const SocketContext = createContext();

export const useSocket = () => {
  const context = useContext(SocketContext);
  if (!context) {
    throw new Error('useSocket must be used within a SocketProvider');
  }
  return context;
};

export const SocketProvider = ({ children }) => {
  const [socket, setSocket] = useState(null);
  const [connected, setConnected] = useState(false);
  const [vehicleUpdates, setVehicleUpdates] = useState({});
  const [binUpdates, setBinUpdates] = useState({});
  const [routeProgress, setRouteProgress] = useState({});

  useEffect(() => {
    const SOCKET_URL = process.env.REACT_APP_SOCKET_URL || 'http://localhost:3000';
    const newSocket = io(SOCKET_URL);

    newSocket.on('connect', () => {
      console.log('Connected to server');
      setConnected(true);
      newSocket.emit('join-room', 'dashboard');
    });

    newSocket.on('disconnect', () => {
      console.log('Disconnected from server');
      setConnected(false);
    });

    // Listen for real-time updates
    newSocket.on('vehicle-update', (data) => {
      setVehicleUpdates(prev => ({
        ...prev,
        [data.vehicleId]: data
      }));
    });

    newSocket.on('bin-update', (data) => {
      setBinUpdates(prev => ({
        ...prev,
        [data.binId]: data
      }));
    });

    newSocket.on('route-progress', (data) => {
      setRouteProgress(prev => ({
        ...prev,
        [data.routeId]: data
      }));
    });

    newSocket.on('bin-collected', (data) => {
      // Handle bin collection updates
      console.log('Bin collected:', data);
    });

    setSocket(newSocket);

    return () => {
      newSocket.close();
    };
  }, []);

  const value = {
    socket,
    connected,
    vehicleUpdates,
    binUpdates,
    routeProgress
  };

  return (
    <SocketContext.Provider value={value}>
      {children}
    </SocketContext.Provider>
  );
};
</boltContext>