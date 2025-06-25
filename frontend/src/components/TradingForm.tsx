import { useState, useEffect } from "react";
import { useMutation, useQueryClient, useQuery } from "@tanstack/react-query";
import { X, AlertCircle, TrendingUp, TrendingDown } from "lucide-react";
import { tradesApi, stocksApi } from "../services/api";
import { TradeRequest } from "../types";

interface TradingFormProps {
  isOpen: boolean;
  onClose: () => void;
  stockSymbol: string;
  tradeType: "BUY" | "SELL";
  userId: string;
}

export default function TradingForm({
  isOpen,
  onClose,
  stockSymbol,
  tradeType,
  userId,
}: TradingFormProps) {
  const [quantity, setQuantity] = useState<number>(1);
  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const queryClient = useQueryClient();

  // Get current stock price
  const { data: stock } = useQuery({
    queryKey: ["stock", stockSymbol],
    queryFn: () => stocksApi.getStockBySymbol(stockSymbol),
    enabled: isOpen && !!stockSymbol,
  });

  // Reset form when modal opens/closes
  useEffect(() => {
    if (isOpen) {
      setQuantity(1);
      setErrors({});
    }
  }, [isOpen, stockSymbol, tradeType]);

  const tradeMutation = useMutation({
    mutationFn: (tradeRequest: TradeRequest) =>
      tradesApi.executeTrade(tradeRequest),
    onSuccess: () => {
      // Invalidate and refetch relevant queries
      queryClient.invalidateQueries({ queryKey: ["portfolio"] });
      queryClient.invalidateQueries({ queryKey: ["trades"] });
      queryClient.invalidateQueries({ queryKey: ["stocks"] });
      onClose();
    },
    onError: (error: any) => {
      const errorMessage =
        error.response?.data?.message ||
        "An error occurred while executing the trade";
      setErrors({ submit: errorMessage });
    },
  });

  const validateForm = () => {
    const newErrors: { [key: string]: string } = {};

    if (!quantity || quantity <= 0) {
      newErrors.quantity = "Quantity must be greater than 0";
    }

    if (!Number.isInteger(quantity)) {
      newErrors.quantity = "Quantity must be a whole number";
    }

    if (quantity > 10000) {
      newErrors.quantity = "Quantity cannot exceed 10,000 shares";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    if (!stock) {
      setErrors({ submit: "Stock information not available" });
      return;
    }

    const tradeRequest: TradeRequest = {
      userId,
      stockSymbol,
      tradeType,
      quantity,
    };

    tradeMutation.mutate(tradeRequest);
  };

  const calculateTotal = () => {
    if (!stock || !quantity) return 0;
    return stock.currentPrice * quantity;
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
    }).format(price);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      <div className="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        {/* Background overlay */}
        <div
          className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"
          onClick={onClose}
        />

        {/* Modal */}
        <div className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
          {/* Header */}
          <div className="bg-white px-6 pt-6 pb-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center">
                {tradeType === "BUY" ? (
                  <TrendingUp className="h-6 w-6 text-success-600 mr-2" />
                ) : (
                  <TrendingDown className="h-6 w-6 text-danger-600 mr-2" />
                )}
                <h3 className="text-lg leading-6 font-medium text-gray-900">
                  {tradeType === "BUY" ? "Buy" : "Sell"} {stockSymbol}
                </h3>
              </div>
              <button
                onClick={onClose}
                className="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <X className="h-6 w-6" />
              </button>
            </div>
          </div>

          {/* Stock Info */}
          {stock && (
            <div className="bg-gray-50 px-6 py-4">
              <div className="flex justify-between items-center">
                <div>
                  <p className="text-sm font-medium text-gray-900">
                    {stock.companyName}
                  </p>
                  <p className="text-xs text-gray-500">Current Price</p>
                </div>
                <div className="text-right">
                  <p className="text-lg font-bold text-gray-900">
                    {formatPrice(stock.currentPrice)}
                  </p>
                  {stock.changePercentage !== undefined &&
                    stock.changePercentage !== null && (
                      <p
                        className={`text-sm ${
                          stock.changePercentage >= 0
                            ? "text-success-600"
                            : "text-danger-600"
                        }`}
                      >
                        {stock.changePercentage >= 0 ? "+" : ""}
                        {stock.changePercentage.toFixed(2)}%
                      </p>
                    )}
                </div>
              </div>
            </div>
          )}

          {/* Form */}
          <form onSubmit={handleSubmit} className="px-6 py-4">
            <div className="space-y-4">
              {/* Quantity Input */}
              <div>
                <label
                  htmlFor="quantity"
                  className="block text-sm font-medium text-gray-700 mb-1"
                >
                  Quantity
                </label>
                <input
                  type="number"
                  id="quantity"
                  value={quantity}
                  onChange={(e) => setQuantity(parseInt(e.target.value) || 0)}
                  className={`w-full px-3 py-2 border rounded-md focus:ring-primary-500 focus:border-primary-500 ${
                    errors.quantity ? "border-red-300" : "border-gray-300"
                  }`}
                  min="1"
                  max="10000"
                  required
                />
                {errors.quantity && (
                  <p className="mt-1 text-sm text-red-600">{errors.quantity}</p>
                )}
              </div>

              {/* Order Summary */}
              {stock && quantity > 0 && (
                <div className="bg-gray-50 rounded-md p-4">
                  <h4 className="text-sm font-medium text-gray-900 mb-2">
                    Order Summary
                  </h4>
                  <div className="space-y-2 text-sm">
                    <div className="flex justify-between">
                      <span className="text-gray-600">Shares:</span>
                      <span className="font-medium">
                        {quantity.toLocaleString()}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Price per share:</span>
                      <span className="font-medium">
                        {formatPrice(stock.currentPrice)}
                      </span>
                    </div>
                    <div className="border-t border-gray-200 pt-2 flex justify-between">
                      <span className="font-medium text-gray-900">Total:</span>
                      <span className="font-bold text-gray-900">
                        {formatPrice(calculateTotal())}
                      </span>
                    </div>
                  </div>
                </div>
              )}

              {/* Error Message */}
              {errors.submit && (
                <div className="flex items-center p-3 bg-red-50 border border-red-200 rounded-md">
                  <AlertCircle className="h-5 w-5 text-red-500 mr-2" />
                  <p className="text-sm text-red-700">{errors.submit}</p>
                </div>
              )}
            </div>

            {/* Actions */}
            <div className="flex space-x-3 mt-6">
              <button
                type="button"
                onClick={onClose}
                className="flex-1 px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={tradeMutation.isPending || !stock}
                className={`flex-1 px-4 py-2 border border-transparent rounded-md text-sm font-medium text-white focus:outline-none focus:ring-2 focus:ring-offset-2 ${
                  tradeType === "BUY"
                    ? "bg-success-600 hover:bg-success-700 focus:ring-success-500"
                    : "bg-danger-600 hover:bg-danger-700 focus:ring-danger-500"
                } disabled:opacity-50 disabled:cursor-not-allowed`}
              >
                {tradeMutation.isPending
                  ? "Processing..."
                  : `${tradeType === "BUY" ? "Buy" : "Sell"} ${stockSymbol}`}
              </button>
            </div>
          </form>

          {/* Market Disclaimer */}
          <div className="bg-gray-50 px-6 py-3">
            <p className="text-xs text-gray-500 text-center">
              Market prices are simulated for demonstration purposes. Orders are
              executed immediately at current market price.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
