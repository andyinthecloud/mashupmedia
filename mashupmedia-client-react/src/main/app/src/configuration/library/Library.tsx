import { Box, Button, Tab, Tabs } from "@mui/material";
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { NotificationType, addNotification } from '../../common/notification/notificationSlice';
import { RootState } from "../../common/redux/store";
import { LibraryPayload, LibraryTypePayload, deleteLibrary, getLibrary } from "../backend/libraryCalls";
import './Library.css';
import LibraryGeneral from "./LibraryGeneral";
import LibraryFiles from "./LibraryFiles";
import LibraryUsers from "./LibraryUsers";


export type TabPanelPayload = {
    value?: number
    index: number
}

export type LibraryPagePayload = {
    libraryPayload: LibraryPayload
    tabPanelPayload: TabPanelPayload
}

const Library = () => {

    const enum FieldNames {
        TYPE = 'libraryTypePayload',
        NAME = 'name',
        PATH = 'path',
        CREATED_ON = 'createdOn',
        CREATED_BY = 'createdBy',
        UPDATED_ON = 'updatedOn',
        UPDATED_BY = 'updatedBy'
    }

    const enum MusicFieldNames {
        ART_IMAGE_PATTERN = 'albumArtImagePattern'
    }

    const { libraryId } = useParams()
    const userToken = useSelector((state: RootState) => state.security.payload?.token)

    const [props, setProps] = useState<LibraryPagePayload>({

        libraryPayload: {
            name: '',
            path: '',
            enabled: true,
            libraryTypePayload: LibraryTypePayload.MUSIC
        },
        tabPanelPayload: {
            value: 0,
            index: 0
        }
    })

    useEffect(() => {
        if (libraryId) {
            getLibrary(+libraryId, userToken)
                .then(response => {
                    const libraryPayload = response.parsedBody || null

                    if (libraryPayload) {
                        setProps(p => ({
                            ...p,
                            libraryPayload
                        }))
                    }
                })
        }

    }, [libraryId, userToken])

    const navigate = useNavigate()

    function handleCancel(): void {
        navigate('/configuration/libraries')
    }

    function handleDeleteLibrary(): void {

        const libraryId = props.libraryPayload.id;
        if (!libraryId) {
            return;
        }

        deleteLibrary(libraryId, userToken)
            .then(() => {
                dispatch(
                    addNotification({
                        message: 'Account deleted',
                        notificationType: NotificationType.SUCCESS
                    })
                )

            })
            .catch(() => {
                dispatch(
                    addNotification({
                        message: 'Unable to delete account.',
                        notificationType: NotificationType.ERROR
                    })
                )
            })

        navigate('/configuration/libraries')
    }

    const dispatch = useDispatch()

    const handleTabChange = (event: React.SyntheticEvent, tabValue: number) => {
        setProps(p => ({
            ...p,
            tabPanelPayload: {
                ...p.tabPanelPayload,
                value: tabValue
            }
        }))
    }

    const userPolicyPayload = useSelector((state: RootState) => state.userPolicy.payload)

    const isShowDeleteButton = (): boolean => {
        return userPolicyPayload?.administrator || false
    }

    const isTabDisabled = (): boolean => {
        return !props.libraryPayload.id
    }

    return (
        <form>
            <h1>Library</h1>

            <Box sx={{ 
                borderBottom: 1, 
                borderColor: 'divider',
                marginBottom: 5 }}>
                <Tabs
                    value={props.tabPanelPayload.value}
                    onChange={handleTabChange}
                    textColor="secondary"
                    indicatorColor="secondary"
                    aria-label="library tabs"
                >
                    <Tab value={0} label="General" />
                    <Tab value={1} label="Files" disabled={isTabDisabled()}/>
                    <Tab value={2} label="Shares" disabled={isTabDisabled()}/>
                </Tabs>
            </Box>

            <LibraryGeneral {...props} />
            <LibraryFiles {...props} />
            <LibraryUsers {...props} />


            <div className="new-line right">
                <Button variant="contained" color="secondary" type="button" onClick={handleCancel}>
                    Cancel
                </Button>

                {isShowDeleteButton() &&
                    <Button variant="contained" color="secondary" type="button" onClick={handleDeleteLibrary}>
                        Delete
                    </Button>
                }


            </div>

        </form>
    )
}

export default Library