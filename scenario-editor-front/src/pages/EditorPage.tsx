import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Button,
  Typography,
  Paper,
  Tabs,
  Tab,
  Alert,
  CircularProgress,
  Chip,
  TextField,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from '@mui/material';
import {
  Save as SaveIcon,
  Publish as PublishIcon,
  CheckCircle as ValidateIcon,
  ArrowBack as BackIcon,
} from '@mui/icons-material';
import { useDraftStore } from '@/store/draftStore';
import { useAuthStore } from '@/store/authStore';

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel({ children, value, index }: TabPanelProps) {
  return (
    <div role="tabpanel" hidden={value !== index}>
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

export default function EditorPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuthStore();
  const {
    currentDraft,
    validationResult,
    isLoading,
    error,
    fetchDraftById,
    updateDraft,
    validateDraft,
    publishDraft,
  } = useDraftStore();

  const [tabValue, setTabValue] = useState(0);
  const [publishDialogOpen, setPublishDialogOpen] = useState(false);
  const [courseSlug, setCourseSlug] = useState('');

  const isEditor = user?.role === 'ADMIN' || user?.role === 'EDITOR';
  const canPublish = user?.role === 'ADMIN';

  useEffect(() => {
    if (id) {
      fetchDraftById(id);
    }
  }, [id]);

  const handleSave = async () => {
    if (!id || !currentDraft) return;

    try {
      await updateDraft(id, {
        scenarioJson: currentDraft.scenarioJson,
      });
      alert('Draft saved successfully');
    } catch (err) {
      // Error handled by store
    }
  };

  const handleValidate = async () => {
    if (!id) return;

    try {
      await validateDraft(id);
    } catch (err) {
      // Error handled by store
    }
  };

  const handlePublish = async () => {
    if (!id || !courseSlug.trim()) return;

    try {
      await publishDraft(id, courseSlug);
      setPublishDialogOpen(false);
      alert('Draft published successfully');
    } catch (err) {
      // Error handled by store
    }
  };

  if (isLoading && !currentDraft) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
        <CircularProgress />
      </Box>
    );
  }

  if (!currentDraft) {
    return (
      <Box>
        <Alert severity="error">Draft not found</Alert>
        <Button startIcon={<BackIcon />} onClick={() => navigate('/drafts')} sx={{ mt: 2 }}>
          Back to Drafts
        </Button>
      </Box>
    );
  }

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Box display="flex" alignItems="center" gap={2}>
          <Button startIcon={<BackIcon />} onClick={() => navigate('/drafts')}>
            Back
          </Button>
          <Typography variant="h4">{currentDraft.name}</Typography>
          <Chip label={currentDraft.status} color="primary" size="small" />
        </Box>

        <Box display="flex" gap={1}>
          {isEditor && (
            <>
              <Button startIcon={<SaveIcon />} variant="outlined" onClick={handleSave}>
                Save
              </Button>
              <Button startIcon={<ValidateIcon />} variant="outlined" onClick={handleValidate}>
                Validate
              </Button>
            </>
          )}
          {canPublish && (
            <Button
              startIcon={<PublishIcon />}
              variant="contained"
              onClick={() => setPublishDialogOpen(true)}
              disabled={validationResult !== null && !validationResult.isValid}
            >
              Publish
            </Button>
          )}
        </Box>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {validationResult && (
        <Alert severity={validationResult.isValid ? 'success' : 'error'} sx={{ mb: 2 }}>
          {validationResult.isValid ? (
            'Validation passed! Draft is ready to publish.'
          ) : (
            <>
              <Typography variant="subtitle2">Validation failed:</Typography>
              <ul>
                {validationResult.errors.map((err, idx) => (
                  <li key={idx}>
                    {err.field}: {err.message}
                  </li>
                ))}
              </ul>
            </>
          )}
        </Alert>
      )}

      <Paper>
        <Tabs value={tabValue} onChange={(_, v) => setTabValue(v)}>
          <Tab label="Course Info" />
          <Tab label="Sessions" />
          <Tab label="JSON Preview" />
        </Tabs>

        <TabPanel value={tabValue} index={0}>
          <Typography variant="h6" gutterBottom>
            Course Information
          </Typography>
          <Box sx={{ mt: 2 }}>
            <Typography>Name: {currentDraft.scenarioJson?.meta?.name || 'N/A'}</Typography>
            <Typography>
              Description: {currentDraft.scenarioJson?.meta?.description || 'N/A'}
            </Typography>
            <Typography>
              Version: {currentDraft.scenarioJson?.meta?.version || 'N/A'}
            </Typography>
            <Typography>
              Category: {currentDraft.category || 'N/A'}
            </Typography>
          </Box>
        </TabPanel>

        <TabPanel value={tabValue} index={1}>
          <Typography variant="h6" gutterBottom>
            Sessions
          </Typography>
          {!currentDraft.scenarioJson?.sessions || Object.keys(currentDraft.scenarioJson.sessions).length === 0 ? (
            <Alert severity="info">No sessions defined yet</Alert>
          ) : (
            Object.entries(currentDraft.scenarioJson.sessions).map(([sessionId, session]: [string, any]) => (
              <Paper key={sessionId} sx={{ p: 2, mb: 2 }}>
                <Typography variant="subtitle1">
                  {session.name || sessionId}
                </Typography>
                <Typography variant="caption" display="block" sx={{ mt: 1 }}>
                  Duration: {session.duration_minutes || 'N/A'} minutes
                </Typography>
                <Typography variant="caption" display="block">
                  Blocks: {session.blocks?.length || 0}
                </Typography>
              </Paper>
            ))
          )}
        </TabPanel>

        <TabPanel value={tabValue} index={2}>
          <Typography variant="h6" gutterBottom>
            JSON Preview
          </Typography>
          <Paper
            sx={{
              p: 2,
              backgroundColor: '#f5f5f5',
              overflow: 'auto',
              maxHeight: '500px',
            }}
          >
            <pre style={{ margin: 0, fontSize: '12px' }}>
              {JSON.stringify(currentDraft.scenarioJson, null, 2)}
            </pre>
          </Paper>
        </TabPanel>
      </Paper>

      <Dialog open={publishDialogOpen} onClose={() => setPublishDialogOpen(false)}>
        <DialogTitle>Publish Draft</DialogTitle>
        <DialogContent>
          <Typography variant="body2" color="text.secondary" paragraph>
            Enter the course slug for this scenario. This will create or update a course.
          </Typography>
          <TextField
            autoFocus
            fullWidth
            label="Course Slug"
            placeholder="anxiety-stress-management"
            value={courseSlug}
            onChange={(e) => setCourseSlug(e.target.value)}
            helperText="Use lowercase letters, numbers, and hyphens"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPublishDialogOpen(false)}>Cancel</Button>
          <Button
            onClick={handlePublish}
            variant="contained"
            disabled={!courseSlug.trim()}
          >
            Publish
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
