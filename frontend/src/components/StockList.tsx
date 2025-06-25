import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Search, TrendingUp, TrendingDown } from "lucide-react";
import { stocksApi } from "../services/api";
import { Stock } from "../types";

interface StockListProps {
  onTradeClick: (symbol: string, type: "BUY" | "SELL") => void;
}

export default function StockList({ onTradeClick }: StockListProps) {
  const [searchTerm, setSearchTerm] = useState("");

  const {
    data: stocks,
    isLoading,
    error,
  } = useQuery({
    queryKey: ["stocks"],
    queryFn: stocksApi.getAllStocks,
    refetchInterval: 30000, // Refetch every 30 seconds
  });

  const filteredStocks =
    stocks?.filter(
      (stock) =>
        stock.symbol.toLowerCase().includes(searchTerm.toLowerCase()) ||
        stock.companyName.toLowerCase().includes(searchTerm.toLowerCase())
    ) || [];

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
    }).format(price);
  };

  const formatPercentage = (percentage?: number) => {
    if (percentage === undefined || percentage === null) return "--";
    const sign = percentage >= 0 ? "+" : "";
    return `${sign}${percentage.toFixed(2)}%`;
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="card">
        <div className="card-body text-center">
          <p className="text-red-600">
            Error loading stocks. Please try again later.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-900">Market Data</h2>
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
          <input
            type="text"
            placeholder="Search stocks..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-primary-500 focus:border-primary-500 w-64"
          />
        </div>
      </div>

      {/* Stock Grid */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {filteredStocks.map((stock) => (
          <div
            key={stock.symbol}
            className="card hover:shadow-lg transition-shadow"
          >
            <div className="card-body">
              {/* Stock Header */}
              <div className="flex justify-between items-start mb-3">
                <div>
                  <h3 className="font-bold text-lg text-gray-900">
                    {stock.symbol}
                  </h3>
                  <p className="text-sm text-gray-600 truncate">
                    {stock.companyName}
                  </p>
                </div>
                <div className="text-right">
                  <p className="font-bold text-xl text-gray-900">
                    {formatPrice(stock.currentPrice)}
                  </p>
                  {stock.changePercentage !== undefined && (
                    <div
                      className={`flex items-center ${
                        stock.changePercentage >= 0
                          ? "text-success-600"
                          : "text-danger-600"
                      }`}
                    >
                      {stock.changePercentage >= 0 ? (
                        <TrendingUp className="h-4 w-4 mr-1" />
                      ) : (
                        <TrendingDown className="h-4 w-4 mr-1" />
                      )}
                      <span className="text-sm font-medium">
                        {formatPercentage(stock.changePercentage)}
                      </span>
                    </div>
                  )}
                </div>
              </div>

              {/* Stock Details */}
              {(stock.openPrice ||
                stock.highPrice ||
                stock.lowPrice ||
                stock.volume) && (
                <div className="grid grid-cols-2 gap-2 text-xs text-gray-500 mb-4">
                  {stock.openPrice && (
                    <div>Open: {formatPrice(stock.openPrice)}</div>
                  )}
                  {stock.highPrice && (
                    <div>High: {formatPrice(stock.highPrice)}</div>
                  )}
                  {stock.lowPrice && (
                    <div>Low: {formatPrice(stock.lowPrice)}</div>
                  )}
                  {stock.volume && (
                    <div>Volume: {stock.volume.toLocaleString()}</div>
                  )}
                </div>
              )}

              {/* Action Buttons */}
              <div className="flex space-x-2">
                <button
                  onClick={() => onTradeClick(stock.symbol, "BUY")}
                  className="btn-success flex-1 text-sm"
                >
                  Buy
                </button>
                <button
                  onClick={() => onTradeClick(stock.symbol, "SELL")}
                  className="btn-danger flex-1 text-sm"
                >
                  Sell
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {filteredStocks.length === 0 && searchTerm && (
        <div className="text-center py-8">
          <p className="text-gray-500">
            No stocks found matching "{searchTerm}"
          </p>
        </div>
      )}
    </div>
  );
}
