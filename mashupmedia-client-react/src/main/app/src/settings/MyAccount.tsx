import { Checkbox, FormControlLabel, FormGroup, TextField } from "@mui/material"
import { Box } from "@mui/system"

const MyAccount = () => {

    const isAdministrator = () => true
    const isMyAccount = () => true

    return (
        <form>
            <h1>Edit user</h1>


            <div className="new-line">
                <Box sx={{ color: 'primary.main' }}>
                <FormGroup>
                    <FormControlLabel control={<Checkbox defaultChecked />} label="Administrator" />
                    <FormControlLabel disabled control={<Checkbox />} label="Enabled" />
                </FormGroup>
                </Box>
            </div>

            <div className="new-line">
                <TextField name="username" label="Username"
                    fullWidth={true} />
            </div>

            <div className="new-line">
                <TextField name="name" label="Name"
                    fullWidth={true} />
            </div>



        </form>
    )



}


export default MyAccount