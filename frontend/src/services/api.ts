import axios from "axios";
import {
  Stock,
  Portfolio,
  PortfolioSummary,
  Trade,
  TradeRequest,
} from "../types";

const API_BASE_URL = "http://localhost:8080/api";

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor for debugging
api.interceptors.request.use(
  (config) => {
    console.log(`API Request: ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error("API Error:", error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export const stocksApi = {
  getAllStocks: async (): Promise<Stock[]> => {
    const response = await api.get("/stocks");
    return response.data;
  },

  getStock: async (symbol: string): Promise<Stock> => {
    const response = await api.get(`/stocks/${symbol}`);
    return response.data;
  },

  getStockBySymbol: async (symbol: string): Promise<Stock> => {
    const response = await api.get(`/stocks/${symbol}`);
    return response.data;
  },

  searchStocks: async (query: string): Promise<Stock[]> => {
    const response = await api.get(
      `/stocks/search?query=${encodeURIComponent(query)}`
    );
    return response.data;
  },

  getCurrentPrice: async (symbol: string): Promise<number> => {
    const response = await api.get(`/stocks/${symbol}/price`);
    return response.data;
  },

  updateStockPrice: async (symbol: string, newPrice: number): Promise<void> => {
    await api.put(`/stocks/${symbol}/price`, { newPrice });
  },
};

export const portfolioApi = {
  getUserPortfolio: async (userId: string): Promise<Portfolio[]> => {
    const response = await api.get(`/portfolio/user/${userId}`);
    return response.data;
  },

  getUserStockHolding: async (
    userId: string,
    symbol: string
  ): Promise<Portfolio> => {
    const response = await api.get(`/portfolio/user/${userId}/stock/${symbol}`);
    return response.data;
  },

  getPortfolioSummary: async (userId: string): Promise<PortfolioSummary> => {
    const response = await api.get(`/portfolio/user/${userId}/summary`);
    return response.data;
  },

  checkSufficientShares: async (
    userId: string,
    symbol: string,
    quantity: number
  ): Promise<boolean> => {
    const response = await api.get(
      `/portfolio/user/${userId}/stock/${symbol}/shares/${quantity}/check`
    );
    return response.data;
  },
};

export const tradesApi = {
  executeTrade: async (request: TradeRequest): Promise<Trade> => {
    if (request.tradeType === "BUY") {
      const response = await api.post("/trades/buy", request);
      return response.data;
    } else {
      const response = await api.post("/trades/sell", request);
      return response.data;
    }
  },

  buyStock: async (request: TradeRequest): Promise<Trade> => {
    const response = await api.post("/trades/buy", request);
    return response.data;
  },

  sellStock: async (request: TradeRequest): Promise<Trade> => {
    const response = await api.post("/trades/sell", request);
    return response.data;
  },

  getUserTrades: async (userId: string): Promise<Trade[]> => {
    const response = await api.get(`/trades/user/${userId}`);
    return response.data;
  },

  getUserStockTrades: async (
    userId: string,
    symbol: string
  ): Promise<Trade[]> => {
    const response = await api.get(`/trades/user/${userId}/stock/${symbol}`);
    return response.data;
  },

  getTrade: async (tradeId: number): Promise<Trade> => {
    const response = await api.get(`/trades/${tradeId}`);
    return response.data;
  },

  cancelTrade: async (tradeId: number, userId: string): Promise<void> => {
    await api.put(`/trades/${tradeId}/cancel?userId=${userId}`);
  },

  validateTrade: async (request: TradeRequest): Promise<boolean> => {
    const response = await api.post("/trades/validate", request);
    return response.data;
  },
};

export default api;
