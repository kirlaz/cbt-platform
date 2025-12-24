import { apiClient } from './api';
import { LoginRequest, RegisterRequest, AuthResponse, User } from '@/types/auth';

class AuthService {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>('/auth/login', credentials);

    // Store tokens
    apiClient.setTokens(response.accessToken, response.refreshToken);

    // Store user info
    localStorage.setItem('user', JSON.stringify(response.user));

    return response;
  }

  async register(data: RegisterRequest): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>('/auth/register', data);

    // Store tokens
    apiClient.setTokens(response.accessToken, response.refreshToken);

    // Store user info
    localStorage.setItem('user', JSON.stringify(response.user));

    return response;
  }

  async logout(): Promise<void> {
    try {
      await apiClient.post('/auth/logout');
    } finally {
      // Always clear local data even if API call fails
      apiClient.clearTokens();
      localStorage.removeItem('user');
    }
  }

  getCurrentUser(): User | null {
    const userJson = localStorage.getItem('user');
    if (!userJson) return null;

    try {
      return JSON.parse(userJson);
    } catch {
      return null;
    }
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('accessToken');
  }

  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    return user?.role === role;
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  isEditor(): boolean {
    return this.hasRole('ADMIN') || this.hasRole('EDITOR');
  }
}

export const authService = new AuthService();
