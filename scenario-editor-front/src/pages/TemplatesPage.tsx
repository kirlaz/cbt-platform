import { useEffect } from 'react';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Chip,
  Alert,
  CircularProgress,
} from '@mui/material';
import { useTemplateStore } from '@/store/templateStore';

export default function TemplatesPage() {
  const { templates, isLoading, error, fetchTemplates } = useTemplateStore();

  useEffect(() => {
    fetchTemplates();
  }, []);

  if (isLoading && (!templates || templates.length === 0)) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Block Templates
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {!templates || templates.length === 0 ? (
        <Box textAlign="center" py={8}>
          <Typography variant="h6" color="text.secondary">
            No templates available
          </Typography>
        </Box>
      ) : (
        <Grid container spacing={3}>
          {templates.map((template) => (
            <Grid item xs={12} sm={6} md={4} key={template.id}>
              <Card>
                <CardContent>
                  <Box display="flex" justifyContent="space-between" alignItems="start" mb={1}>
                    <Typography variant="h6" component="div">
                      {template.name}
                    </Typography>
                    <Chip label={template.blockType} size="small" color="primary" />
                  </Box>

                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    {template.description || 'No description'}
                  </Typography>

                  <Box mt={2} display="flex" gap={0.5} flexWrap="wrap">
                    <Chip label={template.category} size="small" variant="outlined" />
                    {template.tags?.map((tag) => (
                      <Chip key={tag} label={tag} size="small" variant="outlined" />
                    ))}
                  </Box>

                  <Box mt={2}>
                    <Typography variant="caption" color="text.secondary">
                      Used {template.usageCount} times
                    </Typography>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Box>
  );
}
