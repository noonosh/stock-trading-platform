import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Clock, ArrowUpCircle, ArrowDownCircle, Filter } from "lucide-react";
import { tradesApi } from "../services/api";
import { Trade } from "../types";

interface TradeHistoryProps {
  userId: string;
}

export default function TradeHistory({ userId }: TradeHistoryProps) {
  const [filter, setFilter] = useState<
    "ALL" | "BUY" | "SELL" | "COMPLETED" | "PENDING"
  >("ALL");

  const {
    data: trades,
    isLoading,
    error,
  } = useQuery({
    queryKey: ["trades", userId],
    queryFn: () => tradesApi.getUserTrades(userId),
    refetchInterval: 30000,
  });

  const filteredTrades =
    trades?.filter((trade) => {
      if (filter === "ALL") return true;
      if (filter === "BUY" || filter === "SELL")
        return trade.tradeType === filter;
      if (filter === "COMPLETED" || filter === "PENDING")
        return trade.status === filter;
      return true;
    }) || [];

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
    }).format(price);
  };

  const formatDateTime = (dateTime: string) => {
    return new Date(dateTime).toLocaleString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const getTradeTypeIcon = (type: string) => {
    return type === "BUY" ? (
      <ArrowUpCircle className="h-5 w-5 text-success-600" />
    ) : (
      <ArrowDownCircle className="h-5 w-5 text-danger-600" />
    );
  };

  const getStatusBadge = (status: string) => {
    const baseClasses =
      "inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium";

    switch (status) {
      case "COMPLETED":
        return (
          <span className={`${baseClasses} bg-success-100 text-success-800`}>
            Completed
          </span>
        );
      case "PENDING":
        return (
          <span className={`${baseClasses} bg-warning-100 text-warning-800`}>
            Pending
          </span>
        );
      case "CANCELLED":
        return (
          <span className={`${baseClasses} bg-gray-100 text-gray-800`}>
            Cancelled
          </span>
        );
      default:
        return (
          <span className={`${baseClasses} bg-gray-100 text-gray-800`}>
            {status}
          </span>
        );
    }
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
            Error loading trade history. Please try again later.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header with Filters */}
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-900">Trade History</h2>
        <div className="flex items-center space-x-3">
          <Filter className="h-4 w-4 text-gray-400" />
          <select
            value={filter}
            onChange={(e) => setFilter(e.target.value as any)}
            className="border border-gray-300 rounded-md px-3 py-2 text-sm focus:ring-primary-500 focus:border-primary-500"
          >
            <option value="ALL">All Trades</option>
            <option value="BUY">Buy Orders</option>
            <option value="SELL">Sell Orders</option>
            <option value="COMPLETED">Completed</option>
            <option value="PENDING">Pending</option>
          </select>
        </div>
      </div>

      {/* Trade Cards for Mobile */}
      <div className="block md:hidden">
        {filteredTrades.length > 0 ? (
          <div className="space-y-4">
            {filteredTrades.map((trade) => (
              <div key={trade.id} className="card">
                <div className="card-body">
                  <div className="flex justify-between items-start mb-3">
                    <div className="flex items-center">
                      {getTradeTypeIcon(trade.tradeType)}
                      <div className="ml-3">
                        <p className="font-medium text-gray-900">
                          {trade.stockSymbol}
                        </p>
                        <p className="text-sm text-gray-500">
                          {trade.quantity} shares at {formatPrice(trade.price)}
                        </p>
                      </div>
                    </div>
                    {getStatusBadge(trade.status)}
                  </div>

                  <div className="flex justify-between items-center text-sm text-gray-500">
                    <span>Total: {formatPrice(trade.totalAmount)}</span>
                    <div className="flex items-center">
                      <Clock className="h-4 w-4 mr-1" />
                      {formatDateTime(trade.timestamp)}
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="card">
            <div className="card-body text-center">
              <p className="text-gray-500">
                No trades found for the selected filter.
              </p>
            </div>
          </div>
        )}
      </div>

      {/* Trade Table for Desktop */}
      <div className="hidden md:block card">
        <div className="card-body p-0">
          {filteredTrades.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="table">
                <thead className="table-header">
                  <tr>
                    <th className="table-header-cell">Type</th>
                    <th className="table-header-cell">Symbol</th>
                    <th className="table-header-cell">Quantity</th>
                    <th className="table-header-cell">Price</th>
                    <th className="table-header-cell">Total</th>
                    <th className="table-header-cell">Status</th>
                    <th className="table-header-cell">Date</th>
                  </tr>
                </thead>
                <tbody className="table-body">
                  {filteredTrades.map((trade) => (
                    <tr key={trade.id}>
                      <td className="table-cell">
                        <div className="flex items-center">
                          {getTradeTypeIcon(trade.tradeType)}
                          <span
                            className={`ml-2 font-medium ${
                              trade.tradeType === "BUY"
                                ? "text-success-600"
                                : "text-danger-600"
                            }`}
                          >
                            {trade.tradeType}
                          </span>
                        </div>
                      </td>
                      <td className="table-cell font-medium">
                        {trade.stockSymbol}
                      </td>
                      <td className="table-cell">{trade.quantity}</td>
                      <td className="table-cell">{formatPrice(trade.price)}</td>
                      <td className="table-cell">
                        {formatPrice(trade.totalAmount)}
                      </td>
                      <td className="table-cell">
                        {getStatusBadge(trade.status)}
                      </td>
                      <td className="table-cell">
                        {formatDateTime(trade.timestamp)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="p-6 text-center">
              <p className="text-gray-500">
                No trades found for the selected filter.
              </p>
              <p className="text-sm text-gray-400 mt-2">
                Start trading to see your transaction history here.
              </p>
            </div>
          )}
        </div>
      </div>

      {/* Summary Stats */}
      {filteredTrades.length > 0 && (
        <div className="grid gap-4 md:grid-cols-3">
          <div className="card">
            <div className="card-body">
              <div className="text-center">
                <p className="text-sm font-medium text-gray-500">
                  Total Trades
                </p>
                <p className="text-2xl font-bold text-gray-900">
                  {filteredTrades.length}
                </p>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="card-body">
              <div className="text-center">
                <p className="text-sm font-medium text-gray-500">Buy Orders</p>
                <p className="text-2xl font-bold text-success-600">
                  {filteredTrades.filter((t) => t.tradeType === "BUY").length}
                </p>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="card-body">
              <div className="text-center">
                <p className="text-sm font-medium text-gray-500">Sell Orders</p>
                <p className="text-2xl font-bold text-danger-600">
                  {filteredTrades.filter((t) => t.tradeType === "SELL").length}
                </p>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
