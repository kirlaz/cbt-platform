import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Button,
  Card,
  CardContent,
  CardActions,
  Typography,
  Grid,
  Chip,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Alert,
  CircularProgress,
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Visibility as ViewIcon,
} from '@mui/icons-material';
import { useDraftStore } from '@/store/draftStore';
import { useAuthStore } from '@/store/authStore';
import { DraftStatus } from '@/types/draft';

const statusColors: Record<DraftStatus, 'default' | 'warning' | 'success' | 'primary'> = {
  DRAFT: 'default',
  VALIDATING: 'warning',
  READY: 'success',
  PUBLISHED: 'primary',
};

export default function DraftsPage() {
  const navigate = useNavigate();
  const { user } = useAuthStore();
  const { drafts, isLoading, error, fetchDrafts, createDraft, deleteDraft } = useDraftStore();
  const [openDialog, setOpenDialog] = useState(false);
  const [newDraftName, setNewDraftName] = useState('');
  const [newDraftCategory, setNewDraftCategory] = useState('anxiety');

  const isEditor = user?.role === 'ADMIN' || user?.role === 'EDITOR';

  useEffect(() => {
    fetchDrafts();
  }, []);

  const handleCreateDraft = async () => {
    if (!newDraftName.trim()) return;

    try {
      const draft = await createDraft({
        name: newDraftName,
        category: newDraftCategory,
        version: '1.0.0',
        scenarioJson: {
          meta: {
            title: newDraftName,
            description: '',
            category: newDraftCategory,
          },
          sessions: [],
        },
      });

      setOpenDialog(false);
      setNewDraftName('');
      setNewDraftCategory('anxiety');
      navigate(`/drafts/${draft.id}`);
    } catch (err) {
      // Error handled by store
    }
  };

  const handleDeleteDraft = async (id: string) => {
    if (!confirm('Are you sure you want to delete this draft?')) return;

    try {
      await deleteDraft(id);
    } catch (err) {
      // Error handled by store
    }
  };

  if (isLoading && (!drafts || drafts.length === 0)) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">Scenario Drafts</Typography>
        {isEditor && (
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => setOpenDialog(true)}
          >
            New Draft
          </Button>
        )}
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {!drafts || drafts.length === 0 ? (
        <Box textAlign="center" py={8}>
          <Typography variant="h6" color="text.secondary" gutterBottom>
            No drafts yet
          </Typography>
          {isEditor && (
            <Typography variant="body2" color="text.secondary">
              Click "New Draft" to create your first scenario
            </Typography>
          )}
        </Box>
      ) : (
        <Grid container spacing={3}>
          {drafts.map((draft) => (
            <Grid item xs={12} sm={6} md={4} key={draft.id}>
              <Card>
                <CardContent>
                  <Box display="flex" justifyContent="space-between" alignItems="start" mb={1}>
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                      {draft.name}
                    </Typography>
                    <Chip
                      label={draft.status}
                      color={statusColors[draft.status]}
                      size="small"
                    />
                  </Box>

                  <Box mt={2}>
                    <Typography variant="caption" color="text.secondary">
                      Category: {draft.category || 'N/A'}
                    </Typography>
                    <br />
                    <Typography variant="caption" color="text.secondary">
                      Version: {draft.version}
                    </Typography>
                    <br />
                    <Typography variant="caption" color="text.secondary">
                      Valid: {draft.isValid ? 'Yes' : 'No'}
                    </Typography>
                    <br />
                    <Typography variant="caption" color="text.secondary">
                      Updated: {new Date(draft.updatedAt).toLocaleDateString()}
                    </Typography>
                  </Box>
                </CardContent>

                <CardActions>
                  <IconButton
                    size="small"
                    onClick={() => navigate(`/drafts/${draft.id}`)}
                    title="View/Edit"
                  >
                    {isEditor ? <EditIcon /> : <ViewIcon />}
                  </IconButton>

                  {isEditor && (
                    <IconButton
                      size="small"
                      color="error"
                      onClick={() => handleDeleteDraft(draft.id)}
                      title="Delete"
                    >
                      <DeleteIcon />
                    </IconButton>
                  )}
                </CardActions>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Create New Draft</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Draft Name"
            fullWidth
            required
            value={newDraftName}
            onChange={(e) => setNewDraftName(e.target.value)}
            sx={{ mb: 2 }}
          />
          <TextField
            margin="dense"
            label="Category"
            fullWidth
            value={newDraftCategory}
            onChange={(e) => setNewDraftCategory(e.target.value)}
            placeholder="e.g., anxiety, depression, self-esteem"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button onClick={handleCreateDraft} variant="contained" disabled={!newDraftName.trim()}>
            Create
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
