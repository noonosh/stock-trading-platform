export interface Stock {
  symbol: string;
  companyName: string;
  currentPrice: number;
  changePercentage?: number;
  lastUpdated: string;
  openPrice?: number;
  highPrice?: number;
  lowPrice?: number;
  volume?: number;
}

export interface Portfolio {
  id: number;
  userId: string;
  stockSymbol: string;
  quantity: number;
  averagePurchasePrice: number;
  currentPrice: number;
  totalValue: number;
  totalCost: number;
  gainLoss: number;
  gainLossPercentage: number;
  lastUpdated: string;
}

export interface PortfolioSummary {
  totalValue: number;
  totalCost: number;
  totalGainLoss: number;
  totalGainLossPercentage: number;
  totalPositions: number;
}

export interface Trade {
  id: number;
  userId: string;
  stockSymbol: string;
  tradeType: "BUY" | "SELL";
  quantity: number;
  price: number;
  timestamp: string;
  status: "PENDING" | "EXECUTED" | "FAILED" | "CANCELLED";
  statusMessage?: string;
  totalValue: number;
}

export interface TradeRequest {
  userId: string;
  stockSymbol: string;
  tradeType: "BUY" | "SELL";
  quantity: number;
}

export interface ApiError {
  message: string;
  status: number;
}
